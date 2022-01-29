
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
        pipeline {
            agent any
            environment {
                NEXUS_USER         = credentials('user-admin')
                NEXUS_PASSWORD     = credentials('user-pass')
            }
            parameters {
                choice  name: 'compileTool', choices: ['Gradle', 'Maven'], description: 'Seleccione el empaquetador maven/gradle'
            }
            stages {
                stage("Pipeline"){
                    steps {
                        script{
                            // params.compileTool
                            sh "env"
                            switch(params.compileTool)
                            {
                                case 'Maven':
                                    echo "Maven"
                                    //def ejecucion = load 'maven.groovy'
                                    //ejecucion.call()
                                    maven.call()
                                break;
                                case 'Gradle':
                                    echo "Gradle"
                                    //def ejecucion = load 'gradle.groovy'
                                    //ejecucion.call()
                                    gradle.call()
                                break;
                            }
                        }
                    }
                    post{
                        success{
                            slackSend color: 'good', message: "[Diego Roco] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
                        }
                        failure{
                            slackSend color: 'danger', message: "[Diego Roco] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
                        }
                    }
                }
            }
        }
    
}

return this;