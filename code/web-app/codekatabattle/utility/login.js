function sendLoginReq() {
    if($("#email").val() == "" || $("#password").val() == ""){
        alert("Please fill in all fields");
        return;
    }
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email: $("#email").val(),
            password: $("#password").val(),
        })
    };
    fetch("http://localhost:8086/api/account/sign-in",fetchOptions)
        .then(function (response) {
            if (response.status === "success") {
                localStorage.setItem("logged_in", "true");
                localStorage.setItem("user_id", response.userid);
                localStorage.setItem("user_email", response.email);
                localStorage.setItem("user_role", response.role);
                window.location.href = "index.html";
            } else {
                alert("Credenziali errate. Riprova.");
                if (response.status >= 400) {
                    window.location.href = "signin.html";
                }
            }
        })
        .catch(function (err) {
            alert("Errore durante la richiesta AJAX.");
                window.location.href = "index.html";
        })
};

function sendSignUpReq() {
    if($("#email").val() == "" || $("#password").val() == "" || $("#name").val() == "" || $("#accountType option:selected").length === 0){
        alert("Please fill in all fields");
        return;
    }
    
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email: $("#email").val(),
            password: $("#password").val(),
            fullname: $("#name").val(),
            role: accountType,
        })
    };
    fetch("http://localhost:8086/api/account/sign-up",fetchOptions)
        .then(function (response) {
            if (response.status === "success") {
                localStorage.setItem("logged_in", "true");
                localStorage.setItem("user_id", response.userid);
                localStorage.setItem("user_email", response.email);
                localStorage.setItem("user_role", response.role);
                window.location.href = "index.html";
            } else {
                alert("Credenziali errate. Riprova.");
                if (response.status >= 400) {
                    window.location.href = "signin.html";
                }
            }
        })
        .catch(function (err) {
            alert("Errore durante la richiesta.");
                window.location.href = "index.html";
        })
};
