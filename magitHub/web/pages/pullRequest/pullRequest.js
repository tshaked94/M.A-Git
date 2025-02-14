function categorizePullRequests(pullRequests) {
    var result = {'openPR' : [],
                'closedPR' : []
                };

    $.each(pullRequests || [], function (index, entry) {
        if (pullRequests[index].m_Status === "Open")
            result.openPR.push(pullRequests[index]);
        else
            result.closedPR.push(pullRequests[index]);
    });

    return result;
}

function handlePR(event) {
    //event.data.PRID
    //event.data.userDecision
    var message = "nothing"
    if(event.data.userDecision === "decline")
    {
        do {
            message = prompt("Please enter the decline reason");
        } while (message == null || message === "");
    }

    $.ajax({
        method: 'POST',
        data: {
            "requestType": "handlePR"
            ,"PRID": event.data.PRID
            ,"userDecision" : event.data.userDecision
            ,"message": message
            },
        url: "/magitHub/pullRequest",
        error: function (data) {
            console.log("error was occurred while handling pr via user request");
            if(data.responseText.includes("Dirty"))
            {
                alert("WC status is dirty, the system did not approve the pull request");
            }
        },
        success: function (data) {
            alert("The pull request has been successfully " + event.data.userDecision + "d");
            window.location.reload();
        }
    });


}

function appendPRCardsByStatus(PRArray) {
    var numberOfPR = 0;
    var repositoryName, PRCreator, baseBranch, targetBranch, dateOfCreation, status;
    var lastRowElement, mainContainerElement = $(".main-container");

    $.each(PRArray || [], function (index, entry) {
        if (numberOfPR % 4 === 0) {
            if (entry.m_Status === "Open") {
                $(".closed-pr-header").before($('<div class="row">'));
                $(".row:last").after($('<div class="row mb-4"></div>'));
            } else {
                mainContainerElement.append($('<div class="row"></div>'));
                mainContainerElement.append($('<div class="row mb-4"></div>'));
            }
        }
        repositoryName = entry.m_RRName;
        PRCreator = entry.m_LRUsername;
        baseBranch = entry.m_BaseBranchName;
        targetBranch = entry.m_TargetBranchName;
        dateOfCreation = entry.m_DateOfCreation;
        status = entry.m_Status;

        lastRowElement = $(".row:last");
        if (entry.m_Status === "Open") {
            lastRowElement.append('<div class="col-xl-3 col-md-6 col-sm-12"> <div class="card"> <div class="card-content"> <div class="card-body"> <h4 class="card-title style="font-size:16px"">' + repositoryName + '</h4> <h6 class="card-subtitle text-muted" style="font-size:14px">Pull request creator: ' + PRCreator + '</h6> </div>' +
                '<div class="card-body"> <p class="card-text">Base branch: ' + baseBranch + '<br>Target branch: ' + targetBranch + '<br>Date of creation :' + dateOfCreation + ' <br>Status: ' + status + '</p><a class="card-link pink watch-link" style="font-size:17px">Watch</a> <a class="card-link pink approve-link" href="#" style="font-size:17px">Approve</a><a class="card-link pink decline-link" href="#" style="font-size:17px">Decline</a> </div> </div> </div> </div>');
            $(".watch-link:last").attr("href", "/magitHub/pages/prcontent/prcontent.html?PRID=" + entry.m_RequestID);
            $(".approve-link:last").click({PRID : entry.m_RequestID, userDecision : "approve"}, handlePR);
            $(".decline-link:last").click({PRID : entry.m_RequestID, userDecision : "decline"}, handlePR);
        } else {
            lastRowElement.append('<div class="col-xl-3 col-md-6 col-sm-12"> <div class="card"> <div class="card-content"> <div class="card-body"> <h4 class="card-title style="font-size:16px"">' + repositoryName + '</h4> <h6 class="card-subtitle text-muted" style="font-size:14px">Pull request creator: ' + PRCreator + '</h6> </div>' +
                '<div class="card-body"> <p class="card-text">Base branch: ' + baseBranch + '<br>Target branch: ' + targetBranch + '<br>Date of creation :' + dateOfCreation + ' <br>Status: ' + status + '</p> </div> </div> </div> </div>');
        }
        numberOfPR++;
    });
}

function removePRTitleIfEmpty(PRcategorize) {
    var numberOfOpenPR = PRcategorize.openPR.length;
    var numberOfClosedPR = PRcategorize.closedPR.length;

    if (numberOfOpenPR === 0 && numberOfClosedPR === 0) {
        // there is no open or closed pr
        $(".open-pr-title").text('There are no pull requests at the moment');
        $(".closed-pr-title").text('');
    } else {
        if (PRcategorize.openPR.length === 0) {
            $(".open-pr-title").text('');
        }
        if (PRcategorize.closedPR.length === 0) {
            // delete closed pr title
            $(".closed-pr-title").text('');
        }
    }
}

function appendPullRequestsCards(pullRequests) {
    var categorizedPR = categorizePullRequests(pullRequests);

    removePRTitleIfEmpty(categorizedPR);
    appendPRCardsByStatus(categorizedPR.openPR);
    appendPRCardsByStatus(categorizedPR.closedPR);
}

// initialize pull requests cards
$(function(){
    $.ajax({
        method: 'GET',
        data: {"isLoggedInUser" : "TRUE"
        },
        url: "/magitHub/pages/main/user",
        error: function (data) {
            console.log("error was occurred while retrieving pull requests information");
        },
        success: function (data) {
            appendPullRequestsCards(data.m_PullRequests);
        }
    });

});