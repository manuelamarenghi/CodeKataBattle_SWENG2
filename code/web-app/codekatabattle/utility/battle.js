function GetTeam(){
    //ancora non presente lato server
    const params = new URLSearchParams(queryString);
    const tournamentID = params.get('idT');
    const battleID = params.get('idB');
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userID : localStorage.getItem("user_id"),
            battleID : battleID,
        })
    };
    fetch("http://localhost:8082/api/battle/get-team",fetchOptions)
        .then(response => {
        if (response.status >= 300 ) {
            console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
            window.location.href = "index.html";
        }
        })
        .then(data => {
        console.log(data);
        var teamSection = document.querySelector('.team-section');  
        teamSection.innerHTML = ""; 

        for (var i = 0; i < data.length; i++) {
        var teamMemberDiv = document.createElement('div');
        teamMemberDiv.className = 'team-member';

        teamMemberDiv.innerHTML = `
            <div>ID Team: ${data[i].teamID}</div>
            <div>Score: ${data[i].score}</div>
        `;

        var teamNameDiv = document.createElement('div');
        teamNameDiv.className = 'member-name';
        teamNameDiv.textContent = `Name: ${data[i].teamName}`;
        teamMemberDiv.appendChild(teamNameDiv);                    

        teamSection.appendChild(teamMemberDiv);
        }
        })
        .catch(error => {
            console.error('Errore durante la richiesta HTTP:', error);
            window.location.href = "index.html";
        });
    openModal('TeamModal');
}

function SendEval(){
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userID : localStorage.getItem("user_id"),
            teamID : document.getElementById("teamID").value,
            score : document.getElementById("score").value,
        })
    };
    fetch("http://localhost:8082/api/battle/assign-personal-score",fetchOptions)
          .then(function(response){
            if (response.status >= 300 ) {
                console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
                window.location.href = "index.html";
            }
            closeModal('EvalModal');
          })
}

function registerBattle(){
    const queryString = window.location.search;
    const params = new URLSearchParams(queryString);
    const battleID = params.get('idB');
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            idStudent : localStorage.getItem("user_id"),
            idBattle : document.getElementById("battleID").value,
        })
    };
    fetch("http://localhost:8082/api/battle/join-battle",fetchOptions)
          .then(function(response){
            if (response.status >= 300 ) {
                console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
                alert('Errore nella richiesta al server');            }
          })
}


function InvitesStudent(){
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            idStudent : document.getElementById("userID").value,
            idTeam  : document.getElementById("teamID").value, 
        })
    };
    fetch("http://localhost:8082/api/battle/invite-student-to-team",fetchOptions)
          .then(function(response){
            if (response.status >= 300 ) {
                console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
                alert('Errore nella richiesta al server');            
                window.location.href = "index.html";
            }
            closeModal('InvitesModal');
          })
}