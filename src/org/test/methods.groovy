package org.test

def buildService() {
    sh ''' 
        echo "*** build $servicename"

        currentPwd=$(pwd)

        rm -rf dist
        mkdir dist

        cd $currentPwd/svc/$servicename 
                            
        echo "*** Build counter: $BUILD_NUMBER"

        chmod +x gradlew
        ./gradlew clean build --refresh-dependencies
    '''    
}

return this