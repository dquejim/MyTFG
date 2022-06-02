<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";

//Creamos la conexión
$conexion = mysqli_connect($server,$user,$pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

$sql = "SELECT * FROM tbLocalData" ;
mysqli_set_charset($conexion, "utf8");
if(!$result = mysqli_query($conexion, $sql)) die();

$local = array();

while($row = mysqli_fetch_array($result)) {
            $Id = $row['Id'];
            $Ubication = $row["Ubication"];
            $Fb_link = $row["Fb_link"];
            $Ig_link = $row["Ig_link"];
            $Adress = $row["Adress"];
            $Number = $row["Number"];

        $local[] = array('Id'=>$Id, 'Ubication'=>$Ubication,'Fb_link'=>$Fb_link, 'Ig_link'=>$Ig_link,  'Adress'=>$Adress,'Number'=>$Number );
}
$close = mysqli_close($conexion)or die ("no connection");

$json_string = json_encode($local);

echo $json_string
?>