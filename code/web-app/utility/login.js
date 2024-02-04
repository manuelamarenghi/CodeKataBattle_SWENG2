async function setRole(){
    console.log("searching for user role"+localStorage.getItem("user_id"));
    fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userID: localStorage.getItem("user_id"),
        })
    };
    fetch("http://localhost:8080/api/account/user",fetchOptions)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                localStorage.removeItem("logged_in", localStorage.getItem("logged_in"));
                localStorage.removeItem("user_id", localStorage.getItem("user_id"));
                localStorage.removeItem("user_email", localStorage.getItem("user_email"));
                throw new Error("Errore during the request.Please retry.");
            }
        })
        .then(function (data) {
            console.log(data);
            localStorage.setItem("user_role", data.role);
            window.location.href = "index.html";
        })
        .catch(function (err) {
            alert("Error during the request to the server.");
            localStorage.removeItem("logged_in", localStorage.getItem("logged_in"));
            localStorage.removeItem("user_id", localStorage.getItem("user_id"));
            localStorage.removeItem("user_email", localStorage.getItem("user_email"));
            window.location.href = "index.html";
        })
}

function sendLoginReq() {
    var email= document.getElementById("email").value;
    var password= document.getElementById("password").value;
    if(!email || !password  ){
        alert("Please fill in all fields");
        return;
    }
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email: email,
            password: password,
        })
    };
    fetch("http://localhost:8080/api/account/sign-in", fetchOptions)
        .then(function (response) {
            console.log("Step 1");
            if (response.ok) {
                return response.text();
            } else {
                throw new Error("Credential error. Please retry.");
            }
        })
        .then(function (data) {
            console.log("Step 2");
            console.log(data);
            localStorage.setItem("logged_in", "true");
            localStorage.setItem("user_id", data);
            localStorage.setItem("user_email", email);
            setRole();
            return data;
        })
        .catch(function (err) {
            console.error(err);
            alert("Error while doing the request to the server." +err);
            localStorage.removeItem("logged_in", localStorage.getItem("logged_in"));
            localStorage.removeItem("user_id", localStorage.getItem("user_id"));
            localStorage.removeItem("user_email", localStorage.getItem("user_email"));
            window.location.href = "index.html";
            throw err;
        })
};

function sendSignUpReq() {
    localStorage.removeItem("logged_in",localStorage.getItem("logged_in"));
    localStorage.removeItem("user_id",localStorage.getItem("user_id"));
    localStorage.removeItem("user_email",localStorage.getItem("user_email") );
    localStorage.removeItem("user_role",localStorage.getItem("user_role") );
    var email= document.getElementById("email").value;
    var password= document.getElementById("password").value;
    var name= document.getElementById("name").value;
    var accountType= document.getElementById("accountType").value;
    if(!email || !password || !accountType ){
        alert("Please fill in all fields");
        return;
    }

    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email: email,
            fullName: name,
            password: password,
            role: accountType,
        })
    };
    console.log(fetchOptions);
    fetch("http://localhost:8080/api/account/sign-up",fetchOptions)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                alert("Error in data inserted. Please retry");
            }
        })
        .then(function (data) {
            console.log("Step 2");
            console.log(data);
            localStorage.setItem("logged_in", "true");
            localStorage.setItem("user_id", data);
            localStorage.setItem("user_email", email);
            localStorage.setItem("user_role", accountType);
            window.location.href = "index.html";
            return data;
        })
        .catch(function (err) {
            localStorage.removeItem("logged_in",localStorage.getItem("logged_in"));
            localStorage.removeItem("user_id",localStorage.getItem("user_id"));
            localStorage.removeItem("user_email",localStorage.getItem("user_email") );
            localStorage.removeItem("user_role",localStorage.getItem("user_role") );
            alert("Error while doing the request to the server." +err);
            window.location.href = "index.html";
        })
};
