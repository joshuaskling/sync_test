pipeline 'basic_pipeline', {
  projectName = 'Default'

  formalParameter 'ec_stagesToRun', {
    expansionDeferred = '1'
  }

  stage 'Stage 1', {
    colorCode = '#289ce1'
    pipelineName = 'basic_pipeline'
    task 'echo', {
      command = 'echo "hello"'
      taskType = 'COMMAND'
    }
  }
}
