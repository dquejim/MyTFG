<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";

$userName = $_GET['name'];
$new_password = $_GET['password'];
$new_number = $_GET['number'];
$new_adress = $_GET['adress'];

//Creamos la conexión
$conexion = mysqli_connect($server,$user,$pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

$sql = "UPDATE `tableLogin` SET `password` = ".$new_password.",`number`=".$new_number.",`adress`=".$new_adress." WHERE `user` = ".$userName;
mysqli_set_charset($conexion, "utf8");
if(!$result = mysqli_query($conexion, $sql)) die();

echo $result
?>

