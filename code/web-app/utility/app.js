function createTournament() {
    var deadlineInput = document.getElementById("deadline");
    var deadlineValue = deadlineInput.value ;
    var deadlineDate = new Date(deadlineValue);
    console.log(deadlineDate);
    if (!document.getElementById("TournamentName").value) {
        alert("Please insert a name for the tournament.");
        return;
    }
    else{
        var fetchOptions = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                creatorID: localStorage.getItem("user_id"),
                name: document.getElementById("TournamentName").value,
                regdeadline: deadlineDate,
            })
        };
        console.log(fetchOptions);
        fetch("http://localhost:8080/api/tournament/new-tournament",fetchOptions)
            .then(function (response) {
                if (response.status <300) {
                    alert("Tournament created.");
                } else {
                    alert("Error while creating the tournament. Please retry.");
                }
                closeModal('createTournamentModal');
            })
            .catch(function (err) {
                alert("An error occur while connecting to the server: "+ error);
                closeModal('createTournamentModal');
            })
    }}

function grantPermission(){
    var educatorRoleId = document.getElementById("educatorRole").value;
    var tournamentId = document.getElementById("tournamentId").value;
    var permissionValue = document.querySelector('input[name="permission"]:checked');
    if (permissionValue && permissionValue.value === "yes" && educatorRoleId && tournamentId){
        var fetchOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                tournamentID: tournamentId,
                userID: educatorRoleId,
                creatorID: localStorage.getItem("user_id"),
            }),
        };
        fetch("http://localhost:8080/api/tournament/permission", fetchOptions)
            .then(function (response) {
                if (response.status <300) {
                    alert("Permesso creato.");
                    window.location.href = "tournaments.html";
                } else {
                    alert("Permesso non aggiunto. Riprova.");
                    if (response.status >= 400) {
                        window.location.href = "tournaments.html";
                    }
                }
            })
            .catch(error => {
                alert("An error occur while connecting to the server", error);
            });
    } else {
        alert("Please fill all fields.");
        console.log("Permission not granted");
    }
    closeModal('PermissionModal');
}

function updateUserInfo(){
    var fetchOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            id: localStorage.getItem("user_id"),
            email: document.getElementById("email").value,
            fullName: document.getElementById("fullName").value,
            password: document.getElementById("password").value,
            role: localStorage.getItem("user_role"),
        }),
    }
    console.log(fetchOptions);
    fetch("http://localhost:8080/api/account/update", fetchOptions)
        .then(function (response) {
            if (response.status <300) {
                alert("Informazioni aggiornate con successo.");
                localStorage.removeItem("logged_in", localStorage.getItem("logged_in"));
                localStorage.removeItem("user_id", localStorage.getItem("user_id"));
                localStorage.removeItem("user_email", localStorage.getItem("user_email"));
                localStorage.removeItem("user_role", localStorage.getItem("user_role"));
                window.location.href = "login.html";
            } else {
                alert("Error  while changing personal info.Please retry");
                if (response.status >= 400) {
                    window.location.href = "account.html";
                }
            }
        })
        .catch(function (err) {
            alert("An error occur while connecting to the server: "+ error);
            closeModal('createTournamentModal');
        })

}



