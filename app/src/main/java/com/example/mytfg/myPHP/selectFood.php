<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";
$search_category = $_GET['category'];

//Creamos la conexión
$conexion = mysqli_connect($server,$user,$pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

$sql = "SELECT * FROM tableFood WHERE category = " . $search_category;
mysqli_set_charset($conexion, "utf8");
if(!$result = mysqli_query($conexion, $sql)) die();

$foods = array();

while($row = mysqli_fetch_array($result)) {
            $number = $row['number'];
            $category = $row["category"];
            $product = $row["product"];
            $price = $row["price"];

        $foods[] = array('number'=>$number, 'category'=>$category, 'product'=>$product, 'price'=>$price);
}
$close = mysqli_close($conexion)or die ("no connection");

$json_string = json_encode($foods);

echo $json_string
?>