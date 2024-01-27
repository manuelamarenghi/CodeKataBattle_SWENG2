$("#login-button").click(function () {
        var email = $("#email").val();
        var password = $("#password").val();
        console.log("ciao 1");
        if (!email || !password ) {
            alert("Per favore, compila tutti i campi.");
            return;
        }
        console.log("ciao 2");
        var SignInRequest = {
            email: email,
            password: password
        };
        $.ajax({
  		 type: "POST",
 		 url: "http://localhost:8081/server",
    	         contentType: "application/json",
  		 data: JSON.stringify({ utente: SignInRequest }),
   		 dataType: "json", 
     	       success: function (response) {
                console.log("ciao 3");
                if (response.status === "success") {
                    // Credenziali corrette, redirect o gestisci la sessione
                    alert("Accesso riuscito con ruolo: " + response.role);
                    localStorage.setItem("logged_in", "true");
                    window.location.href = "index.html";
                    
                } else {
                    // Credenziali errate, mostra un messaggio di errore
                    alert("Credenziali errate. Riprova.");

                    // Se l'account non esiste, reindirizza alla pagina di registrazione
                    if (response.reason === "nonexistent_account") {
                        window.location.href = "signin.html";
                    }
                }
            },
            error: function () {
                // Errore durante la richiesta AJAX
                alert("Errore durante la richiesta AJAX.");
            }
        });
    });

