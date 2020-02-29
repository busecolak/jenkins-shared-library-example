def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    def http_proxy = env['envhttp_proxy']
    def https_proxy = env['envhttps_proxy']
    def checkout_branch = params.checkout_branch

    def pipeline_methods = new org.test.methods()

    pipeline {
        agent{
            node{
                label "${pipelineParams.node}"
            }
        }

        environment{
            servicename="${pipelineParams.servicename}"
            giturl="${pipelineParams.giturl}"
            http_proxy="${http_proxy}"
            https_proxy="${https_proxy}"
        }

        parameters{
            string(name: 'checkout_branch', defaultValue: 'develop', description: 'Branch for Checkout')
        }

        stages{
            stage('Clean'){
                steps{              
                    cleanWs()
                }
            }
            stage('CheckOut'){ 
                steps{
                    deleteDir() 
                    checkout([$class: 'GitSCM', branches: [[name: "$checkout_branch"]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: "/svc/$servicename"]]]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '05', url: "$giturl"]]])     
                } 
            }
            stage('Build Service'){ 
                steps{ 
                    script{
                        pipeline_methods.buildService()
                    }
                } 
            }
        }
    }
}

            