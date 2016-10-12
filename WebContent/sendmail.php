<?php
header("Content-Type:text/html; charset=utf-8");
$name=$_POST['name'];
$mail=$_POST['mail'];
$subject=$_POST['subject'];
$content=$_POST['content'];
$datetimepicker=$_POST['datetimepicker'];

$to = "evonne@dinyi.com.tw";

$message = "
<html>
<head>
<title>HTML email</title>
</head>
<body>
<table>
<tr>
<th>姓名：</th>
<td>";
$message .= $name;
$message .="
</td>
</tr>
<tr>
<th>預約類別：</th>
<td>";

$message .= $subject;
$message .="
</td>
</tr>
<tr>
<th>預約時間：</th>
<td>";
$message .=  $datetimepicker;
$message .="
</td>
</tr>
<tr>
<th>附註：</th>
<td>";
$message .=$content;
$message .="
</td>
</tr>
</table>
</body>
</html>
";


$headers = "MIME-Version: 1.0" . "\r\n";
$headers .= "Content-type:text/html;charset=utf-8" . "\r\n";

$headers .= 'From: <jayla@webhost.com>' . "\r\n";
$headers .= 'Cc: evonne.chen0211@gmail.com' . "\r\n";

  if(mail($to,$subject,$message,$headers)):
   echo "Reservation has been sent!";
  else:
   echo "error";
  endif;
?>