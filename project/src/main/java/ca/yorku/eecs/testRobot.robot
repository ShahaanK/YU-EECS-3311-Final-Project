*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds
#https://www.youtube.com/watch?v=2CSXMMqfPt0 if there is an error
Suite Setup       Create Session    localhost    http://localhost:8080

*** Test Cases ***
addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Margot Robbie   actorId=nm10
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
    
addActorFailOne
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addActorFailTwo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Devin    actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Parasites    movieId=nm144444
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFailOne
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    title=Animal Farm    movieId=tt0394
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
    
addMovieFailTwo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    title=Animal Farm    movieId=nm78
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm17482    movieId=nm144444
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200
  
addRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1    movieId=tt1
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addAwardPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    awardId=aw2    name=Golden Globe
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
    ${params}=    Create Dictionary    awardId=aw999    movieId=tt999
    ${resp}=    PUT On Session    localhost    /api/v1/addAwardWinner    json=${params}    headers=${headers}    expected_status=404
    
getActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm94820392
    ${resp}=    GET On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=200

getActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm456
    ${resp}=    GET On Session    localhost    /api/v1/getActor    json=${params}    headers=${headers}    expected_status=404

getMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm19930442
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=200

getMovieFailOne
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=mov123
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=404
    
getMovieFailTwo
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    id=mov123
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    json=${params}    headers=${headers}    expected_status=400

hasRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm10    movieId=nm78
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}    expected_status=200
    Dictionary Should Contain Value    ${resp.json()}    Margot Robbie acted in Barbie
    
hasRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm123    movieId=nm10491843
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}    expected_status=404
    
computeBaconNumberPass
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm94820392
    ${resp}=    GET On Session    localhost    /api/v1/computerBaconNumber    json=${params}    headers=${headers}    expected_status=200

computeBaconNumberFailOne
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm49
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}    expected_status=400
    
computeBaconNumberFailTwo
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=kowp20491
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}    expected_status=404
   
computeBaconPathPass
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm94820392
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}    expected_status=200
    
computeBaconPathFail
	${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm49
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}    expected_status=404