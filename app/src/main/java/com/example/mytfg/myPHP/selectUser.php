<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";
$search_user = $_GET['user'];

//Creamos la conexión
$conexion = mysqli_connect($server,$user,$pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

$sql = "SELECT * FROM tableLogin WHERE user LIKE " . $search_user ;
mysqli_set_charset($conexion, "utf8");
if(!$result = mysqli_query($conexion, $sql)) die();

$users = array();

while($row = mysqli_fetch_array($result)) {
            $user = $row['user'];
            $password = $row["password"];
            $number = $row["number"];
            $adress = $row["adress"];

        $users[] = array('user'=>$user, 'password'=>$password, 'number'=>$number, 'adress'=>$adress);
}
$close = mysqli_close($conexion)or die ("no connection");

$json_string = json_encode($users);

echo $json_string
?>