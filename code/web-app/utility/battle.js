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
            battleId : battleID,
            studentId : localStorage.getItem("user_id"),
        })
    };
    fetch("http://localhost:8080/api/battle/get-team",fetchOptions)
        .then(response => {
        if (response.status >= 300 ) {
            console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
            window.location.href = "index.html";
        }
        })
        .then(data => {
        console.log(data);
        var members = data.members;
        openModal('TeamModal');
        var teamSection = document.getElementById('team-sec');  
        teamSection.innerHTML = '';
        var teamMemberDiv = document.createElement('div');
        teamMemberDiv.className = 'team-member';
        
        teamMemberDiv.innerHTML = `
            <div>ID Team: ${data.teamID}</div>
        `;
        
        for (var i = 0; i < members.length; i++) {
            var teamNameDiv = document.createElement('div');
            teamNameDiv.className = 'member-name';
            teamNameDiv.textContent = `USERID: ${members[i].userID}     |     Name: ${members[i].fullName}`;
            teamMemberDiv.appendChild(teamNameDiv);                    
        }  
        teamSection.appendChild(teamMemberDiv);
        })
        .catch(error => {
            console.error('Errore durante la richiesta HTTP:', error);
            window.location.href = "index.html";
        });
       }

function SendEval(){
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            idTeam : document.getElementById("team_ID").value,
            score : document.getElementById("score").value,
            idEducator : localStorage.getItem("user_id"),
        })
    };
    fetch("http://localhost:8080/api/battle/assign-personal-score",fetchOptions)
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
    fetch("http://localhost:8080/api/battle/join-battle",fetchOptions)
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
    fetch("http://localhost:8080/api/battle/invite-student-to-team",fetchOptions)
          .then(function(response){
            if (response.status >= 300 ) {
                console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
                alert('Errore nella richiesta al server');            
                window.location.href = "index.html";
            }
          })
    closeModal('InvitesModal');
}