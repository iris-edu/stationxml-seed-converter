$(document).ready(function($){
  var hq_url = "//www.iris.edu/hq/api/json-iris-staff/?callback=test";
  var dmc_emps = {}, dmc_names = [], sorted_names = [], table_body = "";
  var content_arr = ['position', 'phone', 'email', 'bio'];
  $.ajax({
    url: hq_url,
    dataType: 'jsonp',
    crossDomain: true,
    success: function(data) {
      $.each(data['iris_staff'], function(i, val) {
        if(val.department == 'Data Management Center') {
          var name = val.title+"_"+val.emp_first;
          var entry = {
            'title': val.title,
            'emp_first': val.emp_first,
            'position': val.emp_position,
            'phone': val.emp_phone,
            'email': val.emp_email,
            'bio': val.emp_bio,
            'photo': val.photo,
          };
          dmc_emps[name] = entry;
          dmc_names.push(name);
        }
      });
    },
    complete: function() {
      if(dmc_names.length > 1) {
        sorted_names = dmc_names.sort();
        $.each(sorted_names, function (i, val) {
          table_body += "<tr>";
          table_body += '<td style="width:130px;">';
          table_body += '<img src="'+dmc_emps[val]['photo']+'" class="img-rounded" style="display:block;margin:0 auto;"/>';
          table_body += '</td><td>';
          table_body += dmc_emps[val]['title']+", "+dmc_emps[val]['emp_first']+"<br/>";
          $.each(content_arr, function(c_i, c_val) {
            table_body += '<span style="text-transform:capitalize;"><strong>'+c_val+'</strong></span>: ';
            if(c_val == "email") {
              table_body += '<a href="mailto:'+dmc_emps[val][c_val]+'">'+dmc_emps[val][c_val]+'</a><br/>';
            } else {
              table_body += dmc_emps[val][c_val]+"<br/>";
            }
          });
          table_body += "</td></tr>";
        });
        $("table#staff > tbody").html(table_body);
        $("span#dmc_number").html(sorted_names.length);
      } else {
        // Success but badly formatted JSON response
        var table_error = ["<tr><td>",
                           '<span class="alert alert-danger">',
                           "Problem with the IRIS HQ data feed ",
                           "(badly formatted JSON response).</span>",
                           "</td></tr>"
                          ].join('');
        $("table#staff > tbody").html(table_error);
        $("span#dmc_number").html("&mdash;");
      }
    },
    error: function(jqXHR, textStatus, errorThrown) {
      var table_error = ["<tr><td>",
                         '<span class="alert alert-danger">',
                         "Problem with IRIS HQ data feed: ",
                         errorThrown,
                         " (",
                         textStatus,
                         ")</span>",
                         "</td></tr>"
                        ].join('');
      $("table#staff > tbody").html(table_error);
      $("span#dmc_number").html("&mdash;");
    }
  });
});
