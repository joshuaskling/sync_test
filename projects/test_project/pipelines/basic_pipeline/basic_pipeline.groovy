pipeline 'basic_pipeline', {
  projectName = 'test_project'

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
