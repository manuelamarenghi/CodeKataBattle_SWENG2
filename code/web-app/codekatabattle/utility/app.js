function loadPage(page) {
    // Usa AJAX per caricare il contenuto della pagina senza ricaricare l'intera pagina
    $.ajax({
        url: page + '.php',
        type: 'GET',
        dataType: 'html',
        success: function(response) {
            // Inserisci il contenuto della pagina nel div #content
            $('#content').html(response);
        },
        error: function() {
            alert('Errore nel caricamento della pagina.');
        }
    });
}
