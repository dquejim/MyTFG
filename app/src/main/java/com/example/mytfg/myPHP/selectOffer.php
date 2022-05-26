<?php

$server = "iesayala.ddns.net";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";

//Creamos la conexión
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

$sql = "SELECT * FROM tableOffer" ;
mysqli_set_charset($conexion, "utf8");
if(!$result = mysqli_query($conexion, $sql)) die();

$offers = array();

while($row = mysqli_fetch_array($result)) {
            $id = $row['id'];
            $name = $row["name"];
            $week_day = $row["week_day"];
            $price = $row["price"];

        $offers[] = array('id'=>$id, 'name'=>$name, 'week_day'=>$week_day, 'price'=>$price);

}
$close = mysqli_close($conexion)or die ("pue no ha podio ser");

$json_string = json_encode($offers);

echo $json_strin