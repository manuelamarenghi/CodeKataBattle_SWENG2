function createTournament() {
    var deadlineInput = document.getElementById("deadline");
    var deadlineValue = deadlineInput.value ;
    var deadlineDate = new Date(deadlineValue);
    var year = deadlineDate.getFullYear() - 1900;
    deadlineDate.setFullYear(year);
    
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            regdeadline: deadlineDate,
            creatorID: localStorage.getItem("user_id"),
        })
    };
    fetch("http://localhost:8084/api/tournament/new-tournament",fetchOptions)
        .then(function (response) {
            if (response.status === "success") {
                alert("Torneo creato con successo.");
                window.location.href = "tournaments.html";
            } else {
                alert("Errore durante la creazione del torneo. Riprova.");
                if (response.status >= 400) {
                    window.location.href = "tournaments.html";
                }
            }
            closeModal('createTournamentModal');
        })
        .catch(function (err) {
            alert("Errore durante la richiesta AJAX.");
            window.location.href = "index.html";
            closeModal('createTournamentModal');    
        })
}

function grantPermission(){
    var educatorRoleId = document.getElementById("educatorRole").value;
    var tournamentId = document.getElementById("tournamentId").value;   
    var permissionValue = document.querySelector('input[name="permission"]:checked');
    if (permissionValue && permissionValue.value === "yes"){
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
    fetch("http://localhost:8084/api/tournament/permission", fetchOptions)
            .then(function (response) {
                if (response.status === "success") {
                    alert("Permesso creato.");
                    window.location.href = "tournaments.html";
                } else {
                    alert("Errore durante la creazione del torneo. Riprova.");
                    if (response.status >= 400) {
                        window.location.href = "tournaments.html";
                    }
                }
                closeModal('createTournamentModal');
            })
            .catch(error => {
                console.error("Errore nella richiesta HTTP:", error);
            });
    } else {
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
                role: localStorage.getItem("role"),
            }),
    }
    fetch("http://localhost:8086/api/account/update", fetchOptions)
        .then(function (response) {
            if (response.status === "success") {
                alert("Informazioni aggiornate con successo.");
                localStorage.removeItem("logged_in");
                localStorage.removeItem("user_id");
                localStorage.removeItem("user_email");
                localStorage.removeItem("user_role");
                window.location.href = "login.html";
            } else {
                alert("Errore durante l'aggiornamento delle informazioni. Riprova.");
                if (response.status >= 400) {
                    window.location.href = "account.html";
                }
            }
        })

}


