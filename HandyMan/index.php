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
$app->get('/', function () {
	echo "This is GET";
});

// POST route .method name
$app->post('/login', 'login');
$app->post('/register','register');
// PUT route
$app->put('/put',function() {
	echo "This is PUT";
});

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
	//$app->response()->header("Content-Type","application/json");
	$status = false;
	// $json_data = $app->request()->getBody();
	// $data = json_encode($json_data);
	// $username = $data->username;
	// //$password = md5($data->password);
	// $password = $data->password;

	$username = $app->request->post('username');
	$password = $app->request->post('password');
	$password = sha1($password);
	try{
		$db = connectDB();
		$sql = "select * from user where password='$password' and username='$username'";
		$result = $db->query($sql);
		$fetch = $result->fetch(PDO::FETCH_OBJ);
		//var_dump($fetch);
		if(empty($fetch)){
			echo json_encode(to_json(false, "Wrong Username or Password"));
		}else{
			$res = array(
					'status' => true,
					'address' => $fetch->address,
					'username' => $fetch->username,
					'password' => $fetch->password,
					'name'=> $fetch->name,
					'phone'=> $fetch->phone
			);
			echo json_encode($res);
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong";
	}
	
}

function register(){
	$app = \Slim\Slim::getInstance();
	//$app->response()->header("Content-Type","application/json");
	//$status = false;
// 	$json_data = $app->request()->getBody();
// 	$data = json_decode($json_data);
	$username = $app->request->post('username');
	$password = sha1($app->request->post('password'));
	$address = $app->request->post('address');
	$name = $app->request->post('name');
	$phone = $app->request->post('phone');

	try{
		$db = connectDB();
		$sql_check = "select * from user where username='$username'";
		$check = $db->query($sql_check);
		$fetch = $check->fetch(PDO::FETCH_OBJ);
	
		if(empty($fetch)){
			$sql = "insert into user (username, password, name,phone,address) values ('$username','$password','$name','$phone','$address')";
			$result = $db->query($sql);
			$res = to_json(true, "Thank you for registering");
			echo $res;
		}
		else{
			
			$res = to_json(false, "User already exists");
			echo $res;
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong" . $databaseERROR->getMessage();
	}
	
}
function to_json($status,$message){
	$row = array('status' => $status, 'message' => $message);
	return json_encode($row);
}
function connectDB(){
	try{
		// silahkan ganti dbname,password ke database yang benar
		$dsn = 'mysql:dbname=handyman;host=127.0.0.1';
		$dbuser = 'root';
		$password = '';	
		$conn = new PDO($dsn,$dbuser,$password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		return $conn;
	}
	catch(PDOException $asd){
		echo 'Error 123';
	}
}
/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
