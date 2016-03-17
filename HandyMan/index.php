<?php
/**
 * Step 1: Require the Slim Framework
 *
 * If you are not using Composer, you need to require the
 * Slim Framework and register its PSR-0 autoloader.
 *
 * If you are using Composer, you can skip this step.
 */
require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();

/**
 * Step 2: Instantiate a Slim application
 *
 * This example instantiates a Slim application using
 * its default settings. However, you will usually configure
 * your Slim application now by passing an associative array
 * of setting names and values into the application constructor.
 */
$app = new \Slim\Slim();

/**
 * Step 3: Define the Slim application routes
 *
 * Here we define several Slim application routes that respond
 * to appropriate HTTP request methods. In this example, the second
 * argument for `Slim::get`, `Slim::post`, `Slim::put`, `Slim::patch`, and `Slim::delete`
 * is an anonymous function.
 */

// GET route
$app->get('/');

// POST route .method name
$app->post('/post');

// PUT route
$app->put('/put');

// PATCH route
$app->patch('/patch', function () {
    echo 'This is a PATCH route';
});

// DELETE route
$app->delete(
    '/delete',
    function () {
        echo 'This is a DELETE route';
    }
);
function login(){
	$app = \Slim\Slim::getInstance();
	$app->response()->header("Content-Type","application/json");
	$status = false;
	$json_data = $app->request()->getBody();
	$data = json_encode($json_data);
	$username = $data->username;
	$password = md5($data->password);
	
	try{
		$db = connectDB();
		$sql = "select * from pengguna where password='$password' and username='$username'";
		$result = $db->query($sql);
		$fetch = $result->fetch(PDO::FETCH_OBJ);
		if(empty($fetch)){
			echo json_encode(to_json(false, "Wrong Username or Password"));
		}else{
			$sql_address = "select * from alamat where username='$username'";
			$result_address = $db->query($sql_address);
			$fetch_address = $result->fetchAll(PDO::FETCH_OBJ);
			$res = array(
					'status' => true,
					'username' => $fetch['username'],
					'address' => $fetch_address['alamat']
			);
			echo $res;
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong";
	}
	
}
function to_json($status,$message){
	$row = array('status' => $status, 'message' => $message);
	return json_encode($row);
}
function connectDB(){
	try{
		$dbname = 'root';
		$password = '';	
		$conn = new PDO('mysql:host=localhost; dbname=dbname',$dbname,$password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	}
	catch(PDOException $asd){
		echo 'Error';
	}
	return $conn;
}
/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
