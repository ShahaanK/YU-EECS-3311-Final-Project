*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      10 seconds
#https://www.youtube.com/watch?v=2CSXMMqfPt0 if there is an error
Suite Setup       Setup Session

*** Keywords ***
Setup Session
    ${auth}=    Create List    neo4j    12345678
    Create Session    localhost    http://localhost:8080    auth=${auth}

*** Test Cases ***
addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Rowan Witt    actorId=nm1
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
    
addActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon    actorId=nm1
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addActorFailTwo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Devin    actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Parasite    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Animal Farm    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

addRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

getActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=200

getActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm456
    ${resp}=    PUT On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=400

getMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=200

getMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=mov123
    ${resp}=    PUT On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=400

hasRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1    movieId=tt1
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=200

hasRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm123    movieId=nm10491843
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=200