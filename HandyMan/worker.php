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
$app->get('/get',function() {
	echo "This is GET";
});

// POST route .method name
$app->post('/login', 'login');
$app->post('/getorder','getOrderAndUpdateLocation');
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
	$error = false;
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
		$sql = "select * from worker where password='$password' and username='$username'";
		$result = $db->query($sql);
		$fetch = $result->fetch(PDO::FETCH_OBJ);
		//var_dump($fetch);
		if(empty($fetch)){
			echo json_encode(to_json(true, "Wrong Username or Password"));
		}else{
			$res = array(
					'error' => false,
					'username' => $fetch->username,
					'password' => $fetch->password,
					'name' => $fetch->name,
					'photo'=> $fetch->photo,
					'address' => $fetch->address,
					'latitude' => $fetch->latitude,
					'longitude' => $fetch->longitude,
					'tag' => $fetch->tag,
					'rating' => $fetch->rating
			);
			echo json_encode($res);
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong";
	}

	
}

function getOrderAndUpdateLocation(){
	$app = \Slim\Slim::getInstance();
	$error = false;
	$status = $app->request->post('status'); 
	$latitude = $app->request->post('latitude');
	$longitude = $app->request->post('longitude');
	$username = $app->request->post('username');
	try{
		$db = connectDB();
		$sql_update = "UPDATE worker SET latitude='$latitude', longitude='$longitude' WHERE username='$username'";
		$result_update = $db->query($sql_update);

		$sql = "SELECT * FROM user_order WHERE order_status='$status'";
		$result = $db->query($sql);
		$fetch = $result->fetchall(PDO::FETCH_ASSOC);
	
		if(empty($fetch)){
			echo json_encode(to_json(true, "There are no Order"));
		}
		else{
			$res = array();
			foreach ($fetch as $row) {
				array_push(
					$res,
					array(
						'error' => false,
						'user_username' => $row['user_username'],
						'date' => $row['date'],
						'order_status' => $row['order_status'],
						'total_worker' => $row['total_worker'],
						'category' => $row['category'],
						'rating' => $row['rating'],
						'review'=> $row['review'],
						'details'=>$row['details'],
						'address'=> $row['address'],
						'latitude'=> $row['latitude'],
						'longitude' => $row['longitude']
					)
				);
			}
			echo json_encode($res);
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong" . $databaseERROR->getMessage();
	}
}


function to_json($error,$message){
	$row = array('error' => $error, 'message' => $message);
	return json_encode($row);
}




function connectDB(){
	try{
		// silahkan ganti dbname,password ke database yang benar
		$dsn = 'mysql:dbname=handyman;host=127.0.0.1';
		$dbuser = 'root';
		$password = 'root';	
		$conn = new PDO($dsn,$dbuser,$password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		return $conn;
	}
	catch(PDOException $asd){
		echo 'Error when trying to connect to database';
	}
}
/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
