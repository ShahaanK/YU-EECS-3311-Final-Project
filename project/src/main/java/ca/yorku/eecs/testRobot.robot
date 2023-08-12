*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds
#https://www.youtube.com/watch?v=2CSXMMqfPt0 if there is an error
Suite Setup       Create Session    localhost    http://localhost:8080

*** Test Cases ***
addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Ilir Dema   actorId=nm1937491s
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
    
addActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Devin    actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=EECS3311    movieId=nm144444d
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    title=Animal Farm    movieId=tt0394
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
    
addRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1937491s    movieId=nm144444d
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200
  
addRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1    name=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addAwardPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    awardId=aw2g    name=Golden Globe Two
    ${resp}=    PUT On Session    localhost    /api/v1/addAward    json=${params}    headers=${headers}    expected_status=200

addAwardFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    awardId=aw1    name=Golden Globe
    ${resp}=    PUT On Session    localhost    /api/v1/addAward    json=${params}    headers=${headers}    expected_status=400

addAwardWinnerPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    awardId=aw2    movieId=nm78
    ${resp}=    PUT On Session    localhost    /api/v1/addAwardWinner    json=${params}    headers=${headers}    expected_status=200

addAwardWinnerFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    awardId=aw999    name=tt999
    ${resp}=    PUT On Session    localhost    /api/v1/addAwardWinner    json=${params}    headers=${headers}    expected_status=400
        
getActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm105993842
    ${resp}=    GET On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=200

getActorFail404
    # This tests 404 error
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm456
    ${resp}=    GET On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=404

getActorFail400
    # This tests 400 error
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=nm454654456
    ${resp}=    GET On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=400

getMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm19329423
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=200

getMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=mov123
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=400


hasRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm10    movieId=nm78
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}    expected_status=200
    
hasRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm123    name=nm10491843
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}    expected_status=400
    
computeBaconNumberPass
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm94820392
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}    expected_status=200

computeBaconNumberFail
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=nm49
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}    expected_status=400
    
computeBaconPathPass
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm94820392
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}    expected_status=200
    
computeBaconPathFail
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=nm49
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}    expected_status=400
    