function GetTeam(){
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
                console.log(response.json());
            }
            else{ return response.json();}
        })
        .then(data => {
            console.log(data);
            var members = data.participantsName;
            openModal('TeamModal');
            var teamSection = document.getElementById('team-sec');
            teamSection.innerHTML = '';
            var teamMemberDiv = document.createElement('div');
            teamMemberDiv.className = 'team-member';

            teamMemberDiv.innerHTML = `
            <div>ID Team: ${data.teamId}</div>
        `;
            for (var i=0;i<members.length;i++) {
                var teamNameDiv = document.createElement('div');
                teamNameDiv.className = 'member-name';
                teamNameDiv.textContent = `Name: ${members[i]}`;
                teamMemberDiv.appendChild(teamNameDiv);
            }
            teamSection.appendChild(teamMemberDiv);
        })
        .catch(error => {
            console.error('Errore during HTTP request:', error);
        });
}

function SendEval(){
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            idTeam : document.getElementById("Team_ID").value,
            score : document.getElementById("Score").value,
            idEducator : localStorage.getItem("user_id"),
        })
    };
    fetch("http://localhost:8080/api/battle/assign-personal-score",fetchOptions)
        .then(function(response){
            if (response.status >= 300 ) {
                console.error('Errore during HTTP request:', response.status, response.statusText);
                window.location.href = "index.html";
            }else{ alert('Score assigned');}
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
            idBattle : battleID,
        })
    };
    fetch("http://localhost:8080/api/battle/join-battle",fetchOptions)
        .then(function(response){
            if (response.status >= 300 ) {
                console.error('Errore durante la richiesta HTTP:', response.status, response.statusText);
                alert('Errore nella richiesta al server');            }
            else{ alert('You have been registered to the battle');}
        })
}


function InvitesStudent(){
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            idStudent : document.getElementById("UserID").value,
            idTeam  : document.getElementById("TeamID").value,
        })
    };
    fetch("http://localhost:8080/api/battle/invite-student-to-team",fetchOptions)
        .then(function(response){
            if (response.status >= 300 ) {
                console.error('Error during HTTP request:', response.status, response.statusText);
                alert('Error during the request to the server');
            }else{ alert('Invitation sent');}
        })
    closeModal('InvitesModal');
}