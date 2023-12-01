pipeline 'test_pipeline_2', {
  disableMultipleActiveRuns = '0'
  disableRestart = '0'
  enabled = '1'
  overrideWorkspace = '0'
  pipelineRunNameTemplate = null
  projectName = 'test_project'
  releaseName = null
  skipStageMode = 'ENABLED'
  templatePipelineName = null
  templatePipelineProjectName = null
  type = null
  workspaceName = null

  formalParameter 'ec_stagesToRun', defaultValue: null, {
    expansionDeferred = '1'
    label = null
    orderIndex = null
    required = '0'
    type = null
  }

  stage 'Stage 1', {
    colorCode = '#289ce1'
    completionType = 'auto'
    condition = null
    duration = null
    parallelToPrevious = null
    pipelineName = 'test_pipeline'
    plannedEndDate = null
    plannedStartDate = null
    precondition = null
    resourceName = null
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      condition = null
      precondition = null
      }

    gate 'POST', {
      condition = null
      precondition = null
      }

    task 'hello', {
      description = null
      actionLabelText = null
      advancedMode = '0'
      afterLastRetry = null
      allowOutOfOrderRun = '0'
      allowSkip = null
      alwaysRun = '0'
      applicationName = null
      applicationProjectName = null
      applicationVersion = null
      artifacts = null
      ciConfigurationName = null
      ciControllerFolder = null
      ciControllerName = null
      ciEndpoint = null
      ciJobBranchName = null
      ciJobFolder = null
      ciJobName = null
      command = 'echo "Hello world"'
      condition = null
      customLabel = null
      deployerExpression = null
      deployerRunType = null
      disableFailure = null
      duration = null
      emailConfigName = null
      enabled = '1'
      environmentName = null
      environmentProjectName = null
      environmentTemplateName = null
      environmentTemplateProjectName = null
      errorHandling = 'stopOnError'
      externalApplicationId = null
      gateCondition = null
      gateType = null
      groupName = null
      groupRunType = null
      hookCommand = null
      hookProcedureName = null
      hookProjectName = null
      hookShell = null
      hookStageSummaryParameters = null
      insertRollingDeployManualStep = '0'
      instruction = null
      notificationEnabled = null
      notificationTemplate = null
      parallelToPrevious = null
      plannedEndDate = null
      plannedStartDate = null
      postp = null
      precondition = null
      releaseManifest = null
      requiredApprovalsCount = null
      resourceName = null
      retryCount = null
      retryInterval = null
      retryType = null
      rollingDeployEnabled = null
      rollingDeployManualStepCondition = null
      rolloutNotificationEnabled = null
      shell = null
      skippable = '0'
      snapshotName = null
      stageSummaryParameters = null
      startingStage = null
      subErrorHandling = null
      subTaskType = null
      subapplication = null
      subpipeline = null
      subpluginKey = null
      subprocedure = null
      subprocess = null
      subproject = null
      subrelease = null
      subreleasePipeline = null
      subreleasePipelineProject = null
      subreleaseSuffix = null
      subworkflowDefinition = null
      subworkflowStartingState = null
      taskProcessType = null
      taskType = 'COMMAND'
      triggerType = null
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  trigger 'test_trigger', {
    description = ''
    accessTokenPublicId = 'jrbzb4ozl912pivjiilayzpz2o9omh'
    actualParameter = [
      'ec_stagesToRun': '["Stage 1"]',
    ]
    applicationName = null
    enabled = '1'
    environmentName = null
    environmentProjectName = null
    environmentTemplateName = null
    environmentTemplateProjectName = null
    insertRollingDeployManualStep = '0'
    pipelineName = 'test_pipeline'
    pluginKey = 'EC-Github'
    pluginParameter = [
      'commitStatusEvent': 'false',
      'includeBranches': 'main',
      'includeCommitStatuses': 'success',
      'includePrActions': '',
      'prEvent': 'true',
      'pushEvent': 'true',
      'repositories': '/sync_test',
    ]
    procedureName = null
    processName = null
    quietTimeMinutes = '0'
    releaseName = null
    rollingDeployEnabled = null
    rollingDeployManualStepCondition = null
    rollingDeployPhases = null
    runDuplicates = '0'
    scmSyncName = null
    serviceAccountName = 'test_service_account'
    snapshotName = null
    triggerType = 'webhook'
    webhookName = 'default'
    webhookSecretCredentialName = null
    webhookSecretCredentialProjectName = null

    // Custom properties

    property 'ec_trigger_state', {
      propertyType = 'sheet'
    }
  }

  // Custom properties

  property 'ec_counters', {

    // Custom properties
    pipelineCounter = '2'
  }
}
