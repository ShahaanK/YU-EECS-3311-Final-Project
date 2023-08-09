*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds
#https://www.youtube.com/watch?v=2CSXMMqfPt0 if there is an error
Suite Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***
addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=George Orwell    actorId=nm1
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

addActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Devin actorId=nm1
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Parasite    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Animal Farm    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addBookPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    author=George Orwell    title=1984
    ${resp}=    PUT On Session    localhost    /api/book/addBook    json=${params}    headers=${headers}    expected_status=200

addBookFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    author=George Orwell    name=Animal Farm
    ${resp}=    PUT On Session    localhost    /api/book/addBook    json=${params}    headers=${headers}    expected_status=400

printBookPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    author=George Orwell    title=1984
    ${resp}=    GET On Session    localhost    /api/book/printBook    params=${params}    headers=${headers}    expected_status=200
    Dictionary Should Contain Value    ${resp.json()}    George Orwell wrote 1984
    
# ***Keywords*** NEED TO BE ADDED