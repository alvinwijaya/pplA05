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

	try{
		$db = connectDB();
		$sql = "select * from user where password='$password' and username='$username'";
		$result = $db->query($sql);
		$fetch = $result->fetch(PDO::FETCH_OBJ);
		//var_dump($fetch);
		if(empty($fetch)){
			echo json_encode(to_json(false, "Wrong Username or Password"));
		}else{
			$user_id = $fetch->id;
			$sql_address = "select * from user_address where user_id='$user_id'";
			$result_address = $db->query($sql_address);
			$fetch_address = $result_address->fetch(PDO::FETCH_OBJ);
			//var_dump($fetch_address);
			$res = array(
					'status' => true,
					'alamat' => $fetch_address->alamat,
					'user_id' => $user_id,
					'username' => $fetch->username,
					'password' => $fetch->password,
					'nama'=> $fetch->nama
			);
			echo json_encode($res);
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong";
	}
	
}

function register(){
	$app = \Slim\Slim::getInstance();
	$app->response()->header("Content-Type","application/json");
	//$status = false;
	$json_data = $app->request()->getBody();
	$data = json_decode($json_data);
	$username = $data->username;
	$password = md5($data->password);
	$address = $data->address;
	$name = $data->name;
	$link_gmaps = $data->link_gmaps;
	// $password = $data->password;

	//$username = $app->request->post('username');
	//$password = $app->request->post('password');

	try{
		$db = connectDB();
		$sql_check = "select * from user where username='$username'";
		$check = $db->exec($sql_check);
		if(empty($check)){
			$sql = "insert into user (username, password, nama) values ('$username','$password',$name) ";
			$result = $db->exec($sql);
			if(!empty($result)){
				$sql_address = "insert into user_address (user_id, alamat,link_gmaps) values ('$user_id','$alamat','$link_gmaps')";
				$result = $db->exec("$sql_address");
			}
		}
		else{
			
		}
		
		
		//$fetch = $result->fetch(PDO::FETCH_OBJ);
		//var_dump($fetch);
		//if(empty($fetch)){
		//	echo json_encode(to_json(false, "Wrong Username or Password"));
		echo "Thank you for your register";
		// }else{
		// 	$sql_address = "select * from alamat where usernameFK='$username'";
		// 	$result_address = $db->query($sql_address);
		// 	$fetch_address = $result_address->fetch(PDO::FETCH_OBJ);
		// 	//var_dump($fetch_address);
		// 	$res = array(
		// 			'status' => true,
		// 			'username' => $fetch_address->usernameFK,
		// 			'address' => $fetch_address->alamat
		// 	);
		// 	echo json_encode($res);
		// }
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
