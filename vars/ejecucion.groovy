
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
    stage(){
    
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
                    post {
                        always {
                            sh "echo 'fase always executed post'"
                        }

                        success {
                            sh "echo 'fase success'"
                        }

                        failure {
                            sh "echo 'fase failure'"
                        }
                    }
                }
            }
        }
    }
}

return this;