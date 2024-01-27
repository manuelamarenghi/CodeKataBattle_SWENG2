$("#submit-button").click(function (){
    $(".spinner-bourder").slideToggle();
        var email = $("#email").val();
        var password = $("#password").val();
        var fullName = $("#fullName").val();
        if (!email || !password ) {
            alert("Per favore, compila tutti i campi.");
            return;
        }
        var UpdateRequest = {
            id: id,
            email: email,
            fullName: fullName,
            password: password,
            role: role
        };
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/server",
            contentType: "application/json",
            data: {
                utente: JSON.stringify(UpdateRequest)  
            },
            dataType: "json", 
            success: function (response){

            }
        });
});
