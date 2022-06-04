<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";

//Creamos la conexión
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

//generamos la consulta
$name = $_GET["name"];
$password = $_GET["password"];
$number = $_GET["number"];
$adress =  $_GET["adress"];
$fav_food =  $_GET["fav_food"];

$sql = "INSERT INTO tableLogin (user, password , number, adress,fav_food) VALUES ('$name','$password','$number','$adress','$fav_food')";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
echo $sql;


if (mysqli_query($conexion, $sql)) {
      echo "New record created successfully";
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");


?>