#!/usr/bin/perl -w
use MIME::Base64;

sub error {
    my ($message) = @_;
    print <<End_of_Error;
Content-type: text/html
Status: 500 CGI Error

$message
End_of_Error
}

sub getCGIDIR {
  if ($0=~m#^(.*)\\#) {
      $cgi_dir = "$1";
  } elsif ($0=~m#^(.*)/# ) {
      $cgi_dir = "$1";
  } else  {`pwd` =~ /(.*)/;
      $cgi_dir = "$1";
  }
  return $cgi_dir;
}

# ======================
# parse posted variables
# ======================
if ($ENV{'REQUEST_METHOD'} eq "POST") {
  read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
} else {
  $buffer=$ENV{'QUERY_STRING'};
}
@pairs = split(/&/, $buffer);

foreach $pair (@pairs) {
   ($name, $value) = split(/=/, $pair);
   $value =~ tr/+/ /;
   $value =~ s/\%([A-Fa-f0-9]{2})/pack('C', hex($1))/seg;  # decode string
   $vars{$name}=$value;
}



# build filename
$filename = getCGIDIR() . "/submissions/" . $vars{"sessionid"} . "-" . $vars{"userid"} . ".xml";
# ===============================
# main engine
# ===============================
$result="unknown";

if ($vars{"action"} eq "isCompleted") {
  if (-e $filename) {
    $result="0";  # we have that submission
  }
  else
  {
    $result="-1:10";   # we dont have this submission
  }
}
elsif ($vars{"action"} eq "submit") {

  if (-e $filename) { # we allready have this submission, this should have already been checked with isCompleted action!
    $result="-1:20"; # resubmission
  }
  else
  {
    $result="0";
    open (SUBMITFILE, ">" . $filename) || error("-1:21"); # Error creating file
    print SUBMITFILE decode_base64($vars{"answers"});  # Error writing to file
    close (SUBMITFILE);  # Error closing file
  }

}
elsif ($vars{"action"} eq "ping") {

    $result="pong";
}
else
{
  $result="-1:99"; # Unknown action
}


print "Content-type: text/html\r\n\r\n";
print $result;
