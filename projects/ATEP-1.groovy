project 'ATEP-1', {
  tracked = '1'
  workspaceName = 'default'

  credential 'ATEP_DesignDB', userName: 'admin', {
    credentialType = 'LOCAL'
  }

  credential 'ATEP_EIB', userName: 'admin', {
    credentialType = 'LOCAL'
  }

  credential 'ATEP_NCM', userName: 'admin', {
    credentialType = 'LOCAL'
  }

  credential 'ATEP_Network', userName: 'admin', {
    credentialType = 'LOCAL'
  }

  credential 'ATEP_SM9', userName: 'admin', {
    credentialType = 'LOCAL'
  }

  credential 'ATEP_SourceControl', userName: 'admin', {
    credentialType = 'LOCAL'
  }

  pluginConfiguration 'ATEP_git_config', {
    field = [
      'authType': 'password',
      'credential': 'credential',
      'debugLevel': '0',
      'ignoreSSLErrors': 'false',
      'library': 'jgit',
    ]
    pluginKey = 'EC-Git'

    addCredential 'credential', {
      passwordRecoveryAllowed = '1'
      userName = 'admin'
    }
  }

  pluginConfiguration 'SNOW_config', {
    field = [
      'credential': 'credential',
      'debug_level': '0',
      'host': 'CHANGEME',
      'ignoreSSLErrors': '0',
      'use_native_extension': '1',
    ]
    pluginKey = 'EC-ServiceNow'

    addCredential 'credential', {
      passwordRecoveryAllowed = '1'
      userName = 'admin'
    }
  }

  procedure 'Automation_DTCN_Ansible_Generic_Xgrade', {
    resourceName = 'local'
    timeLimit = '0'

    formalParameter 'input_hostname', {
      description = 'List of switches'
      label = 'Hosts'
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_target_version', defaultValue: 'Latest', {
      description = 'Target Version for Xgrade'
      label = 'Target Version'
      orderIndex = '2'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_stage', defaultValue: 'assess', {
      label = 'Stage'
      options = [
        'Assess': 'assess',
        'Implementation': 'implementation',
      ]
      orderIndex = '3'
      type = 'select'
    }

    formalParameter 'input_change_no', {
      label = 'Change Number'
      orderIndex = '4'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_username', {
      description = 'Username will be generated dynamically based on logged in account.'
      label = 'Username'
      orderIndex = '5'
      type = 'entry'
    }

    step 'Inject SSH Key', {
      command = '''ATEP_SourceControl_SSH_KEY=$(ectool getFullCredential ATEP_SourceControl --value password)

echo "$ATEP_SourceControl_SSH_KEY" > /home/danny.chan_priv/.ssh/id_rsa

chmod 600 /home/danny.chan_priv/.ssh/id_rsa'''
      errorHandling = 'abortProcedure'
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      shell = '/bin/bash'
      timeLimit = '0'
      timeLimitUnits = 'seconds'

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_SourceControl'
      }
    }

    step 'Clone Playbooks from GitLab', {
      errorHandling = 'abortProcedure'
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      subprocedure = 'Clone'
      subproject = '/plugins/EC-Git/project'
      timeLimit = '0'
      timeLimitUnits = 'seconds'
      actualParameter 'branch', 'ddta-2427-update-add-stack-pause-and-wait'
      actualParameter 'commit', ''
      actualParameter 'config', '/projects/ATEP/pluginConfigurations/ATEP_EC-Git'
      actualParameter 'depth', ''
      actualParameter 'gitRepoFolder', '/home/danny.chan_priv/ATEP/service-catalogue'
      actualParameter 'mirror', 'false'
      actualParameter 'overwrite', 'true'
      actualParameter 'pathspecs', ''
      actualParameter 'referenceFolder', ''
      actualParameter 'repoUrl', 'git@ate-gitlab.dtcnservices.batx.com.au:defenceautomation/service-catalogue.git'
      actualParameter 'resultPropertySheet', '/myJob/clone'
      actualParameter 'shallowSubmodules', 'false'
      actualParameter 'submodules', 'false'
      actualParameter 'tag', ''
    }

    step 'Clone Inventory from GitLab', {
      errorHandling = 'abortProcedure'
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      subprocedure = 'Clone'
      subproject = '/plugins/EC-Git/project'
      timeLimit = '0'
      timeLimitUnits = 'seconds'
      actualParameter 'branch', 'cloudbees-testing'
      actualParameter 'commit', ''
      actualParameter 'config', '/projects/ATEP/pluginConfigurations/ATEP_EC-Git'
      actualParameter 'depth', ''
      actualParameter 'gitRepoFolder', '/home/danny.chan_priv/ATEP/inventory'
      actualParameter 'mirror', 'false'
      actualParameter 'overwrite', 'true'
      actualParameter 'pathspecs', ''
      actualParameter 'referenceFolder', ''
      actualParameter 'repoUrl', 'git@ate-gitlab.dtcnservices.batx.com.au:defenceautomation/mil-inventory.git'
      actualParameter 'resultPropertySheet', '/myJob/clone'
      actualParameter 'shallowSubmodules', 'false'
      actualParameter 'submodules', 'false'
      actualParameter 'tag', ''
    }

    step 'Install Custom Ansible Collections', {
      command = 'ansible-galaxy install -r /home/danny.chan_priv/ATEP/service-catalogue/collections/requirements.yml'
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      timeLimit = '0'
      timeLimitUnits = 'seconds'
    }

    step 'Inject Credentials', {
      command = '''ATEP_SM9_Username=$(ectool getFullCredential ATEP_SM9 --value userName)
ATEP_SM9_Password=$(ectool getFullCredential ATEP_SM9 --value password)

echo sm9_username: "$ATEP_SM9_Username" >> /home/danny.chan_priv/ATEP/inventory/inventory/awx_inventory
echo sm9_password: "$ATEP_SM9_Password" >> /home/danny.chan_priv/ATEP/inventory/inventory/awx_inventory

sed -i \'/^eib_username/d\' /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
sed -i \'/^eib_password/d\' /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
sed -i \'/^ncm_username/d\' /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
sed -i \'/^ncm_password/d\' /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
sed -i \'/^ansible_user/d\' /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
sed -i \'/^ansible_password/d\' /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml

ATEP_EIB_Username=$(ectool getFullCredential ATEP_EIB --value userName)
ATEP_EIB_Password=$(ectool getFullCredential ATEP_EIB --value password)
echo eib_username: "$ATEP_EIB_Username" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
echo eib_password: "$ATEP_EIB_Password" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml

ATEP_NCM_Username=$(ectool getFullCredential ATEP_NCM --value userName)
ATEP_NCM_Password=$(ectool getFullCredential ATEP_NCM --value password)
echo ncm_username: "$ATEP_NCM_Username" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
echo ncm_password: "$ATEP_NCM_Password" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml

ATEP_Network_Username=$(ectool getFullCredential ATEP_Network --value userName)
ATEP_Network_Password=$(ectool getFullCredential ATEP_Network --value password)
echo ansible_user: "$ATEP_Network_Username" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
echo ansible_password: "$ATEP_Network_Password" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
echo passwd: "$ATEP_Network_Password" >> /home/danny.chan_priv/ATEP/inventory/inventory/group_vars/all.yml
'''
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      timeLimit = '0'
      timeLimitUnits = 'seconds'

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_EIB'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_NCM'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_Network'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_SM9'
      }
    }

    step 'Run Add Stack Member Playbook', {
      command = '''export JSNAPY_HOME=/opt/rh/rh-python38/root/usr/local/lib/python3.8/site-packages/jnpr/jsnapy

ATEP_Network_Username=$(ectool getFullCredential ATEP_Network --value userName)
ATEP_Network_Password=$(ectool getFullCredential ATEP_Network --value password)
export ANSIBLE_NET_USERNAME="$ATEP_Network_Username"
export ANSIBLE_NET_PASSWORD="$ATEP_Network_Password"

mkdir -p ~/jsnapy/snapshots
mkdir -p ~/jsnapy/testfiles

if [ $[/myJob/launchedByUser] = \'api_testing\' ] ; then input_username=$[input_username]; else input_username=$[/myJob/launchedByUser]; fi

/opt/rh/rh-python38/root/usr/local/bin/ansible-playbook /home/danny.chan_priv/ATEP/service-catalogue/Automation_DTCN_Ansible_Generic/Automation_DTCN_Ansible_Generic_Xgrade/Automation_DTCN_Ansible_Generic_Xgrade.yml --extra-vars \'{"input_hostname":"$[/myJob/input_hostname]","input_stage":"$[/myJob/input_stage]","input_target_version":"$[/myJob/input_target_version]","input_username":"$input_username","input_change_no":"$[/myJob/input_change_no]"}\' -i /home/danny.chan_priv/ATEP/inventory/inventory -v'''
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      timeLimit = '0'
      timeLimitUnits = 'seconds'

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_Network'
      }
    }

    step 'Cleanup Workspace', {
      alwaysRun = '1'
      command = '''rm -rf /home/danny.chan_priv/.ssh/id_rsa
rm -rf /home/danny.chan_priv/ATEP
rm -rf /home/danny.chan_priv/jsnapy/snapshots
rm -rf /home/danny.chan_priv/jsnapy/testfiles'''
      procedureName = 'Automation_DTCN_Ansible_Generic_Xgrade'
      timeLimit = '0'
      timeLimitUnits = 'seconds'
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'input_stage', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties

              property 'text', value: 'Assess'

              property 'value', value: 'assess'
            }

            property 'option2', {

              // Custom properties

              property 'text', value: 'Implementation'

              property 'value', value: 'implementation'
            }
            optionCount = '2'

            property 'type', value: 'list'
          }
          formType = 'standard'
        }
      }
    }
  }

  procedure 'Automation_DTCN_Ansible_Switch_AddStack', {
    resourceName = 'local'
    timeLimit = '0'

    formalParameter 'input_hostname', {
      description = 'List of switch'
      label = 'Hosts'
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_stage', defaultValue: 'assess', {
      label = 'Stage'
      options = [
        'Assess': 'assess',
        'Implementation': 'implementation',
      ]
      orderIndex = '2'
      type = 'select'
    }

    formalParameter 'input_change_no', {
      label = 'Change Number'
      orderIndex = '3'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_username', defaultValue: 'default', {
      description = 'Username will be generated dynamically based on logged in account.'
      label = 'Username'
      orderIndex = '4'
      renderCondition = '${input_stage} == \'not_a_stage\''
      type = 'entry'
    }

    step 'git_clone', {
      procedureName = 'Automation_DTCN_Ansible_Switch_AddStack'
      subprocedure = 'Clone'
      subproject = '/plugins/EC-Git/project'
      timeLimit = '0'
      timeLimitUnits = 'seconds'
      workspaceName = 'default'
      actualParameter 'config', '/projects/ATEP/pluginConfigurations/ATEP_git_config'
      actualParameter 'mirror', 'false'
      actualParameter 'overwrite', 'false'
      actualParameter 'repoUrl', 'test'
      actualParameter 'resultPropertySheet', '/myJob/clone'
      actualParameter 'shallowSubmodules', 'false'
      actualParameter 'submodules', 'false'
    }

    step 'Prepare Workspace', {
      command = '''ATEP_SourceControl_SSH_KEY=$(ectool getFullCredential ATEP_SourceControl --value password)
mkdir -p .ssh

echo "$ATEP_SourceControl_SSH_KEY" > .ssh/id_rsa
chmod 600 .ssh/id_rsa

GIT_SSH_COMMAND="ssh -i $COMMANDER_WORKSPACE_UNIX/.ssh/id_rsa -o IdentitiesOnly=yes -o StrictHostKeyChecking=no" git clone -b ddta-2427-update-add-stack-pause-and-wait git@ate-gitlab.dtcnservices.batx.com.au:defenceautomation/service-catalogue.git ./service-catalogue
GIT_SSH_COMMAND="ssh -i $COMMANDER_WORKSPACE_UNIX/.ssh/id_rsa -o IdentitiesOnly=yes -o StrictHostKeyChecking=no" git clone -b cloudbees-testing git@ate-gitlab.dtcnservices.batx.com.au:defenceautomation/mil-inventory.git ./mil-inventory

mkdir -p custom_ansible_collections
cd custom_ansible_collections
python ../service-catalogue/collections/install_requirements.py

cd ..
echo $[input_username]
echo $[/myJob/launchedByUser]
if [ $[input_username] = "default" ] ; then input_username=$[/myJob/launchedByUser] ; else input_username=$[input_username]; fi

ectool modifyActualParameter ATEP Automation_DTCN_Ansible_Switch_AddStack \'Prepare Workspace\' input_hostname --value $input_hostname
'''
      errorHandling = 'abortProcedure'
      procedureName = 'Automation_DTCN_Ansible_Switch_AddStack'
      timeLimit = '0'
      timeLimitUnits = 'seconds'

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_SourceControl'
      }
    }

    step 'Run Add Stack Member Playbook', {
      command = '''export JSNAPY_HOME=/opt/rh/rh-python38/root/usr/local/lib/python3.8/site-packages/jnpr/jsnapy
export ANSIBLE_CONFIG=./service-catalogue/ansible.cfg

ATEP_Network_Username=$(ectool getFullCredential ATEP_Network --value userName)
ATEP_Network_Password=$(ectool getFullCredential ATEP_Network --value password)
export ANSIBLE_NET_USERNAME="$ATEP_Network_Username"
export ANSIBLE_NET_PASSWORD="$ATEP_Network_Password"

ATEP_SM9_Username=$(ectool getFullCredential ATEP_SM9 --value userName)
ATEP_SM9_Password=$(ectool getFullCredential ATEP_SM9 --value password)
export SM9_USERNAME="$ATEP_SM9_Username"
export SM9_PASSWORD="$ATEP_SM9_Password"

ATEP_DesignDB_API_Key=$(ectool getFullCredential ATEP_DesignDB --value password)
export DESIGNDB_API_KEY="$ATEP_DesignDB_API_Key"

ATEP_EIB_Username=$(ectool getFullCredential ATEP_EIB --value userName)
ATEP_EIB_Password=$(ectool getFullCredential ATEP_EIB --value password)
export EIB_USERNAME="$ATEP_EIB_Username"
export EIB_PASSWORD="$ATEP_EIB_Password"

ATEP_NCM_Username=$(ectool getFullCredential ATEP_NCM --value userName)
ATEP_NCM_Password=$(ectool getFullCredential ATEP_NCM --value password)
export NCM_USERNAME="$ATEP_NCM_Username"
export NCM_PASSWORD="$ATEP_NCM_Password"

mkdir -p ~/jsnapy/snapshots
mkdir -p ~/jsnapy/testfiles

echo \'--extra-vars {"input_hostname":"$[/myJob/input_hostname]","input_stage":"$[/myJob/input_stage]","input_username":"$[/myJob/input_username]","input_change_no":"$[/myJob/input_change_no]"}\'
echo \'ansible playbook ./service-catalogue/Automation_DTCN_Ansible_Switch/Automation_DTCN_Ansible_Switch_AddStackMember/Automation_DTCN_Ansible_Switch_AddStackMember.yml\'
/opt/rh/rh-python38/root/usr/local/bin/ansible-playbook ./service-catalogue/Automation_DTCN_Ansible_Switch/Automation_DTCN_Ansible_Switch_AddStackMember/Automation_DTCN_Ansible_Switch_AddStackMember.yml --extra-vars \'{"input_hostname":"$[/myJob/input_hostname]","input_stage":"$[/myJob/input_stage]","input_username":"$[/myJob/input_username]","input_change_no":"$[/myJob/input_change_no]"}\' -i ./mil-inventory/inventory -vvv'''
      procedureName = 'Automation_DTCN_Ansible_Switch_AddStack'
      timeLimit = '0'
      timeLimitUnits = 'seconds'

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_DesignDB'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_EIB'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_NCM'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_Network'
      }

      attachCredential {
        credentialName = '/projects/ATEP/credentials/ATEP_SM9'
      }
    }

    step 'Cleanup Workspace', {
      alwaysRun = '1'
      command = '''ls
rm -rf ./.ssh
rm -rf ./service-catalogue
rm -rf ./mil-inventory
rm -rf ./custom_ansible_collections
rm -rf /home/danny.chan_priv/jsnapy/snapshots
rm -rf /home/danny.chan_priv/jsnapy/testfiles
ls'''
      procedureName = 'Automation_DTCN_Ansible_Switch_AddStack'
      timeLimit = '0'
      timeLimitUnits = 'seconds'
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'input_stage', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties

              property 'text', value: 'Assess'

              property 'value', value: 'assess'
            }

            property 'option2', {

              // Custom properties

              property 'text', value: 'Implementation'

              property 'value', value: 'implementation'
            }
            optionCount = '2'

            property 'type', value: 'list'
          }
          formType = 'standard'
        }
      }
    }
  }

  pipeline 'Ansible Playbook Execution', {

    formalParameter 'input_change_no', defaultValue: 'test', {
      label = 'Change Number'
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'incidentDescription', defaultValue: 'test', {
      orderIndex = '2'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_hostname', defaultValue: 'test', {
      label = 'Hosts'
      orderIndex = '3'
      required = '1'
      type = 'entry'
    }

    formalParameter 'pool_name', defaultValue: 'Ansible-test', {
      description = 'The resource pool in which jobs will be distributed.'
      label = 'Pool Name'
      orderIndex = '4'
      required = '1'
      type = 'entry'
    }

    formalParameter 'ec_stagesToRun', {
      expansionDeferred = '1'
    }

    stage 'PRE-PROD', {
      colorCode = '#289ce1'
      pipelineName = 'Ansible Playbook Execution'

      task 'SNOW_status_change', {
        actualParameter = [
          'assigned_to': '',
          'config_name': '/projects/ATEP/pluginConfigurations/SNOW_config',
          'impact': '',
          'priority': '',
          'property_sheet': '/myJobStep',
          'short_description': '$[/myPipelineRuntime/incidentDescription]',
          'sys_id': '$[/myPipelineRuntime/input_change_no]',
          'tagsmap': '',
          'urgency': '',
        ]
        enabled = '0'
        subpluginKey = 'EC-ServiceNow'
        subprocedure = 'UpdateIncident'
        taskType = 'PLUGIN'
      }

      task 'find_agents', {
        description = 'Find all agents associated with this project and prepare to distribute Ansible jobs.'
        command = '''import com.electriccloud.client.groovy.ElectricFlow
import com.electriccloud.client.groovy.models.*

ElectricFlow ef = new ElectricFlow()

def resources = ef.getResourcesInPool(resourcePoolName: "$[pool_name]")
println (resources)
for (resource in resources.resource){
 println(resource.resourceName) 
  
 def jobResult = ef.runProcedure(
                projectName: \'default\',
				procedureName: \'test\')
  
 println (jobResult)
}'''
        shell = 'ec-groovy'
        taskType = 'COMMAND'
      }
    }

    stage 'PROD', {
      colorCode = '#ff7f0e'
      pipelineName = 'Ansible Playbook Execution'

      task 'run_playbooks', {
        command = 'date'
        enabled = '0'
        taskType = 'COMMAND'
      }

      task 'SNOW_status_change', {
        actualParameter = [
          'assigned_to': '',
          'config_name': '/projects/ATEP/pluginConfigurations/SNOW_config',
          'impact': '',
          'priority': '',
          'property_sheet': '/myJobStep',
          'short_description': '$[/myPipelineRuntime/incidentDescription]',
          'sys_id': '$[/myPipelineRuntime/input_change_no]',
          'tagsmap': '',
          'urgency': '',
        ]
        enabled = '0'
        subpluginKey = 'EC-ServiceNow'
        subprocedure = 'UpdateIncident'
        taskType = 'PLUGIN'
      }
    }

    trigger 'variable_change', {
      actualParameter = [
        'ec_stagesToRun': '["PRE-PROD","PROD"]',
      ]
      pipelineName = 'Ansible Playbook Execution'
      pluginKey = 'Trigger-Property'
      pluginParameter = [
        'propName': '/projects/ATEP/test_variable',
      ]
      quietTimeMinutes = '0'
      runDuplicates = '0'
      triggerType = 'polling'

      // Custom properties

      property 'ec_trigger_state', {
        propertyType = 'sheet'
      }
    }

    // Custom properties

    property 'ec_counters', {

      // Custom properties
      pipelineCounter = '7'
    }
  }

  pipeline 'Automation_DTCN_Ansible_Switch_AddStack', {

    formalParameter 'Hosts', {
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'Stage', defaultValue: 'assess', {
      options = [
        'assess': 'assess',
        'implementation': 'implementation',
      ]
      orderIndex = '2'
      required = '1'
      type = 'select'
    }

    formalParameter 'Change_Number', {
      orderIndex = '3'
      required = '1'
      type = 'entry'
    }

    formalParameter 'input_username', defaultValue: 'default', {
      label = 'Username'
      orderIndex = '4'
      type = 'entry'
    }

    formalParameter 'ec_stagesToRun', {
      expansionDeferred = '1'
    }

    stage 'Run Playbook', {
      colorCode = '#289ce1'
      pipelineName = 'Automation_DTCN_Ansible_Switch_AddStack'

      task 'Prepare Workspace', {
        command = '''ATEP_SourceControl_SSH_KEY=$(ectool getFullCredential ATEP_SourceControl --value password)
mkdir -p .ssh

echo "$ATEP_SourceControl_SSH_KEY" > .ssh/id_rsa
chmod 600 .ssh/id_rsa

GIT_SSH_COMMAND="ssh -i $COMMANDER_WORKSPACE_UNIX/.ssh/id_rsa -o IdentitiesOnly=yes -o StrictHostKeyChecking=no" git clone -b ddta-2427-update-add-stack-pause-and-wait git@ate-gitlab.dtcnservices.batx.com.au:defenceautomation/service-catalogue.git ./service-catalogue
GIT_SSH_COMMAND="ssh -i $COMMANDER_WORKSPACE_UNIX/.ssh/id_rsa -o IdentitiesOnly=yes -o StrictHostKeyChecking=no" git clone -b cloudbees-testing git@ate-gitlab.dtcnservices.batx.com.au:defenceautomation/mil-inventory.git ./mil-inventory

mkdir -p custom_ansible_collections
cd custom_ansible_collections
python ../service-catalogue/collections/install_requirements.py

cd ..
echo $[input_username]
echo $[/myJob/launchedByUser]
if [ $[input_username] = "default" ] ; then input_username=$[/myJob/launchedByUser] ; else input_username=$[input_username]; fi

ectool modifyActualParameter ATEP Automation_DTCN_Ansible_Switch_AddStack \'Prepare Workspace\' input_hostname --value $input_hostname
'''
        taskType = 'COMMAND'
      }

      task 'Run Add Stack Member Playbook', {
        command = '''export JSNAPY_HOME=/opt/rh/rh-python38/root/usr/local/lib/python3.8/site-packages/jnpr/jsnapy
export ANSIBLE_CONFIG=./service-catalogue/ansible.cfg

ATEP_Network_Username=$(ectool getFullCredential ATEP_Network --value userName)
ATEP_Network_Password=$(ectool getFullCredential ATEP_Network --value password)
export ANSIBLE_NET_USERNAME="$ATEP_Network_Username"
export ANSIBLE_NET_PASSWORD="$ATEP_Network_Password"

ATEP_SM9_Username=$(ectool getFullCredential ATEP_SM9 --value userName)
ATEP_SM9_Password=$(ectool getFullCredential ATEP_SM9 --value password)
export SM9_USERNAME="$ATEP_SM9_Username"
export SM9_PASSWORD="$ATEP_SM9_Password"

ATEP_DesignDB_API_Key=$(ectool getFullCredential ATEP_DesignDB --value password)
export DESIGNDB_API_KEY="$ATEP_DesignDB_API_Key"

ATEP_EIB_Username=$(ectool getFullCredential ATEP_EIB --value userName)
ATEP_EIB_Password=$(ectool getFullCredential ATEP_EIB --value password)
export EIB_USERNAME="$ATEP_EIB_Username"
export EIB_PASSWORD="$ATEP_EIB_Password"

ATEP_NCM_Username=$(ectool getFullCredential ATEP_NCM --value userName)
ATEP_NCM_Password=$(ectool getFullCredential ATEP_NCM --value password)
export NCM_USERNAME="$ATEP_NCM_Username"
export NCM_PASSWORD="$ATEP_NCM_Password"

mkdir -p ~/jsnapy/snapshots
mkdir -p ~/jsnapy/testfiles

echo \'--extra-vars {"input_hostname":"$[/myJob/input_hostname]","input_stage":"$[/myJob/input_stage]","input_username":"$[/myJob/input_username]","input_change_no":"$[/myJob/input_change_no]"}\'
echo \'ansible playbook ./service-catalogue/Automation_DTCN_Ansible_Switch/Automation_DTCN_Ansible_Switch_AddStackMember/Automation_DTCN_Ansible_Switch_AddStackMember.yml\'
/opt/rh/rh-python38/root/usr/local/bin/ansible-playbook ./service-catalogue/Automation_DTCN_Ansible_Switch/Automation_DTCN_Ansible_Switch_AddStackMember/Automation_DTCN_Ansible_Switch_AddStackMember.yml --extra-vars \'{"input_hostname":"$[/myJob/input_hostname]","input_stage":"$[/myJob/input_stage]","input_username":"$[/myJob/input_username]","input_change_no":"$[/myJob/input_change_no]"}\' -i ./mil-inventory/inventory -vvv'''
        taskType = 'COMMAND'
      }

      task 'Cleanup Workspace', {
        command = '''ls
rm -rf ./.ssh
rm -rf ./service-catalogue
rm -rf ./mil-inventory
rm -rf ./custom_ansible_collections
rm -rf /home/danny.chan_priv/jsnapy/snapshots
rm -rf /home/danny.chan_priv/jsnapy/testfiles
ls'''
        taskType = 'COMMAND'
      }
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'Stage', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties

              property 'text', value: 'assess'

              property 'value', value: 'assess'
            }

            property 'option2', {

              // Custom properties

              property 'text', value: 'implementation'

              property 'value', value: 'implementation'
            }
            optionCount = '2'

            property 'type', value: 'list'
          }
          formType = 'standard'
        }
      }
    }
  }
}
