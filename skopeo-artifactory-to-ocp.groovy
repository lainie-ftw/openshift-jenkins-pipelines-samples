/*************
 * main pipeline: skopeo from one registry to another when one registry is Artifactory
 * @Authors Josh Smith @joshmsmith & Laine Vyvyan @lainie-ftw
 ************/
node(){
    timestamps {
        try {
		//note: these URLs could be params into the pipeline job OR environmental variables
		def artifactoryURL = "<URL of artifactory instance>"
		def openShiftRegistryURL = "<URL of OpenShift internal registry, exposed as a route>"

		//note: these could be params into the pipeline job, that's a better practice overall
		def projectName = "<project in OpenShift>"
		def imageName = "<image name - this can also be the application's name>"
		def tagName = "<tag used to identify the version of the image>"


		stage('Move Image from Artifactory to OpenShift internal registry') {
			//withCredentials is from the Credentials Binding Jenkins plugin: https://jenkins.io/doc/pipeline/steps/credentials-binding/ 
			//in this example, artifactory-creds and openshift-creds would be defined as credentials in Jenkins itself, this plugin allows them to be usable.
                	withCredentials([usernameColonPassword(credentialsId: 'artifactory-creds', variable: 'ARTIFACTORY'), string(credentialsId: 'openshift-creds', variable: 'OPENSHIFT')]) {
                    		sh "skopeo copy --src-creds ${ARTIFACTORY} --dest-creds openshift:${OPENSHIFT} docker://${artifactoryURL}/${projectName}/${imageName}:${tagName} docker://${openShiftRegistryURL}/${projectName}/${imageName}:${tagName}"
                	}
            	}

        } catch (e) {
            currentBuild.result = "FAILED"
            throw e
        }
    }
}
