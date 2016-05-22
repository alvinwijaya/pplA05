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
$app->post('/acceptorder','acceptOrder');
$app->post('/gethistory','getHistory');
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
	$category = $app->request->post('category');
	$split = explode(",", $category);
	try{
		$db = connectDB();
		$sql_update = "UPDATE worker SET latitude='$latitude', longitude='$longitude' WHERE username='$username'";
		$result_update = $db->query($sql_update);
		$res = array();
		foreach ($split as $values) {
			$sql = "SELECT * FROM user_order WHERE order_status='$status' AND category LIKE '%$values%'";
			$result = $db->query($sql);
			$fetch = $result->fetchall(PDO::FETCH_ASSOC);
			if (!empty($fetch)) {
				foreach ($fetch as $row) {
					$user_username = $row['user_username'];
					$sql_get = "SELECT name,phone FROM user WHERE username='$user_username'";
					$result_get = $db->query($sql_get);
					$fetch_get = $result_get->fetch(PDO::FETCH_OBJ);
					array_push(
					$res,
					array(
						'error' => false,
						'user_name' => $fetch_get->name,
						'phone' => $fetch_get->phone,
						'user_order_id' => $row['id'],
						'date' => $row['date'],
						'order_status' => $row['order_status'],
						'total_worker' => $row['total_worker'],
						'category' => $row['category'],
						'rating' => $row['rating'],
						'details'=>$row['details'],
						'address'=> $row['address'],
						'latitude'=> $row['latitude'],
						'longitude' => $row['longitude']
						)
					);
				}
			}
		}
		if (empty($res)) {
			echo to_json(true,"There are no Order");
		} else {
			echo json_encode($res);
		}
	}catch (PDOException $databaseERROR){
		echo "Something went wrong" . $databaseERROR->getMessage();
	}
}

function acceptOrder(){
	$app = \Slim\Slim::getInstance();
	$error = false;
	$status = $app->request->post('status');
	$id = $app->request->post('user_order_id');
	$worker = $app->request->post('worker_username');
	$db = connectDB();
	$sql = "UPDATE user_order SET order_status='$status' WHERE id='$id'";
	$result = $db->query($sql);
	$sql_insert = "INSERT INTO worker_order (user_order_id,worker_username) VALUES ('$id','$worker')";
	$result_insert = $db->query($sql_insert);
	$res = to_json(false,"status update & insert worker");
	echo $res;
}

function getHistory(){
	$app = \Slim\Slim::getInstance();
	$order_list = array();
	try{
		$db = connectDB();
		$worker_username = $app->request->post('worker_username');
		$sql = "SELECT user_order_id FROM worker_order WHERE worker_username='$worker_username'";
		$order = $db->query($sql);
		$fetch_order = $order->fetchAll(PDO::FETCH_ASSOC);
		foreach ($fetch_order as $row) {
			$user_order_id = $row['user_order_id'];
			$sql_getOrder = "SELECT * FROM user_order WHERE id='$user_order_id'";
			$getOrder = $db->query($sql_getOrder);
			$fetch_getOrder = $getOrder->fetchAll(PDO::FETCH_ASSOC);
			foreach ($fetch_getOrder as $values) {
				array_push($order_list, 
				array(
						'user_username' => $values['user_username'],
						'date' => $values['date'],
						'category'=>$values['category'],
						'total_worker' =>$values['total_worker'],
						'address'=>$values['address']
					)
				);
			}
		}
		$result = json_encode($order_list);
		echo $result;
	}catch (Exception $e){
		echo "Something Wrong";
	}
}

function to_json($error,$message){
	$row = array('error' => $error, 'message' => $message);
	return json_encode($row);
}




function connectDB(){
	try{
		// silahkan ganti dbname,password ke database yang benar
		$dsn = 'mysql:dbname=handyman;host=localhost';
		$dbuser = 'ppla05';
		$password = 'ppla05';	
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
