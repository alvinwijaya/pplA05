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
$app->post('/getworker','getWorkerByCategories');
$app->post('/putorder','putOrder');

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

function putOrder(){
	$app = \Slim\Slim::getInstance();
	$error = false;
	
		$db = connectDB();
		$userDetails = $app->request->post('user');
		$jsonUser = json_encode($userDetails);
		$username = $app->request->post('username');
		$category = $app->request->post('category');
		$order_status = $app->request->post('order_status');
		$total_worker = $app->request->post('total_worker');
		$date = $app->request->post('date');
		$rating = $app->request->post("rating");
		$review = $app->request->post("review");
		$details = $app->request->post("details");
		$address = $app->request->post("address");
		$latittude = $app->request->post("latitude");
		$longitude = $app->request->post("longitude");
		
		$sql = "insert into user_order (user_username, date, order_status,total_worker,category,rating,review,details,address,latitude,longitude) values ('$username','$date','$order_status','$total_worker','$category','$rating','$review','$details','$address','$latittude','$longitude')";
		$result = $db->query($sql);

		// $order_id_sql = "SELECT * FROM user_order WHERE id = (SELECT MAX(id) FROM user_order)";
		// $result= $db->query($order_id_sql);
		// $order_id_fetch = $result->fetch(PDO::FETCH_OBJ);
		// $order_id = $order_id_fetch->id;

		// $workers = $app->request->post('workers');
		// $arrJson  = json_decode($workers);
		// foreach ($arrJson as $key => $value) {
		// 	$worker_username = $value->username;
		// 	$worker_order_sql = "INSERT INTO worker_order (user_order_id,worker_username) values ('$order_id','$worker_username')";
		// 	$put_worker_order = $db->query($worker_order_sql);
		// }
	} 

function getWorkerByCategories(){
	$app = \Slim\Slim::getInstance();
	$error = false;
	// categories are array of string
	$category1 = $app->request->post('category1');
	$category2 = $app->request->post('category2');
	try{
		$db = connectDB();
		$sql = "select * from worker where tag LIKE '%$category1%' OR tag LIKE '%$category2%'";
		if (!$category2) {
			$sql = "select * from worker where tag LIKE '%$category1%'";
		}
		
		$result = $db->query($sql);
		$fetch = $result->fetchAll(PDO::FETCH_ASSOC);
		if(empty($fetch)){
			echo json_encode(to_json(true, "can't find worker"));
		}else{
			$res = array();
			foreach ($fetch as $row) {
				array_push(
					$res,
					array(
						'error' => false,
						'username' => $row['username'],
						'address' => $row['address'],
						'tag' => $row['tag'],
						'rating' => $row['rating'],
						'status' => $row['status'],
						'name'=> $row['name'],
						'photo'=> $row['photo'],
						'latitude'=> $row['latitude'],
						'longitude' => $row['longitude']
					)
				);
			}
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
			$res = to_json(false, "Thank you for registering");
			echo $res;
		}
		else{
			
			$res = to_json(true, "User already exists");
			echo $res;
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
// 