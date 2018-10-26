$(function() {
  $("#dpr-form").submit(function(event) {
    event.preventDefault();
    event.stopPropagation();
    $.ajax({
      url: $(this).attr("action"),
      data: {
        network: $("#dpr-network").val(),
        station: $("#dpr-station").val(),
        id: $("#dpr-id").val(),
        output: 'json',
      },
      success: function(resp) {
        var modulus_max = 5;
        if(resp.total > 50 && resp.total <= 100) { modulus_max = 10; }
        if(resp.total > 100 && resp.total <= 500) { modulus_max = 25; }
        if(resp.total > 500 && resp.total <= 1000) { modulus_max = 50; }
        if(resp.total > 1000 && resp.total <= 2000) { modulus_max = 100; }
        if(resp.total > 2000 && resp.total <= 5000) { modulus_max = 250; }
        if(resp.total > 5000) { modulus_max = 500; }
        var output = "<p><strong>Number of data problem reports: "+ resp.total+"</strong><br/>";
        if(resp.total > 10) {
          output += "Jump to: ";
          for(var i=1;i<=resp.total;i++) {
            if(i % modulus_max == 0) {
              output += '<a href="#'+i+'">'+i+'</a> | ';
            }
          }
        }
        if(resp.total > 0) {
          $.each(resp.entries, function(index, value) {
            output += '<pre id="'+index+'">';
            output += value;
            output += "</pre>";
            output += "\n";
          });
        }
        $("#dpr-output").html(output);
      }
    });
  });
});
