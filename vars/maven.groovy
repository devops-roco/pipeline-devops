
def call(stages){

    build()
    test()
    jar()
    sonar()
    curl()
    nexus()
    descarga()
    levantar()
    testear()

}


def build() {    
    def listaDeStages = stages.split(';')
    sh "echo ${listaDeStages}"
    env.STAGE = "Paso 1: Compilacion"
    stage("$env.STAGE") {
        sh "mvn clean compile -e"
    }
}

def test() {
    env.STAGE = "Paso 2: Test"
    stage("$env.STAGE") {
        sh "mvn clean test -e"
    }
}

def jar() {
    env.STAGE = "Paso 3: Empaquetado"
    stage("$env.STAGE") {
        sh "mvn clean package -e"
    }
}

def sonar() {
    env.STAGE = "Paso 4: Sonar - Análisis Estático"
    stage("$env.STAGE") {
        sh "echo 'Análisis Estático!'"
        withSonarQubeEnv('sonarqube') {
            sh 'mvn sonar:sonar -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build'
        }
    }
}

def curl() {
    env.STAGE = "Paso 5: Curl Springboot Maven sleep 20"
    stage("$env.STAGE") {
        sh "java -jar build/DevOpsUsach2020-0.0.1.jar &"
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}
  
def nexus() {
    env.STAGE = "Paso 6: Subir a Nexus"
    stage("$env.STAGE") {
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'devops-usach-nexus',
        packages: [
            [
                $class: 'MavenPackage',
                mavenAssetList: [
                    [
                        classifier: '',
                        extension: '.jar',
                        filePath: 'build/DevOpsUsach2020-0.0.1.jar'
                    ]
                ],
                mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020',
                    groupId: 'com.devopsusach2020',
                    packaging: 'jar',
                    version: '0.0.1'
                ]
            ]   
        ]
    }
}

def descarga() {
    env.STAGE = "Paso 7: Descargar artefacto subido anteriormente desde Nexus"
    stage("$env.STAGE") {
        sh ' curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD "http://nexus:8081/repository/devops-usach-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
    }
}

def levantar() {
    env.STAGE = "Paso 8: Levantar Artefacto Jar descargado"
    stage("$env.STAGE") {
        sh 'nohup bash java -jar DevOpsUsach2020-0.0.1.jar & >/dev/null'
    }
}

def testear() {
    env.STAGE = "Paso 9: Testear Artefacto - Dormir(Esperar 20sg)"
    stage("$env.STAGE") {
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}

return this;
