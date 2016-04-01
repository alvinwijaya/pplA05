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
					'username' => $fetch->username,
					'password' => $fetch->password,
					'name' => $fetch->name,
					'photo'=> $fetch->photo,
					'address' => $fetch->address,
					'latitude' => $fetch->latitude,
					'longitude' => $fetch->longitude,
					'tag' => $fetch->tag,
					'rating' => $fetch->rating,
					'status' => $fetch->status
			);
			echo json_encode($res);
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong";
	}
	
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
		echo 'Error when trying to connect to databse';
	}
}
/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
