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

//$app->post('/giverating','giveRating');

$app->post('/getmeworker','getMeWorker');
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

//given username get all worker
function getMeWorker(){
	$app = \Slim\Slim::getInstance();
	try {
		$db = connectDB();
		$username = $app->request->post('username');
		$worker_list = array();
		$worker_data_list = array();
		//get all order from database
		$order_sql = "SELECT * FROM user_order WHERE user_username='$username'";
		$order = $db->query($order_sql);
		$fetch_order = $order->fetchAll(PDO::FETCH_ASSOC);
		foreach ($fetch_worker as $row) {
			//get_all_worker
			$id = $row['id'];
			$worker_sql = "SELECT worker_username FROM worker_order WHERE worker_user_id='$id'";
			$worker = $db->query($worker_sql);
			$fetch_worker = $worker->fetchAll(PDO::FETCH_ASSOC);
			foreach ($fetch_worker as $my_worker) {
				# code...
				if(!in_array($my_worker, worker_list)){
					array_push($worker_list, $my_worker);
					$worker_data_sql = "SELECT * FROM worker WHERE username='$my_worker'";
					$worker_data = $db->query($worker_data_sql);
					$fetch_worker_data = $worker_data->fetch(PDO::FETCH_ASSOC);
					array_push($worker_data_list,
						array(
							'username' => $fetch_worker_data["username"],
							'name' => $fetch_worker_data["name"],
							'photo' => $fetch_worker_data["photo"],
							'tag' => $fetch_worker_data["tag"],
							'rating' => $fetch_worker_data["rating"]
							)
					);
				}
			}
		}
	} catch (Exception $e) {
		echo $e;
	}
	
	echo json_encode($worker_data_list);

}

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

} 

function getWorkerByCategories(){
	$app = \Slim\Slim::getInstance();
	$error = false;
	// categories are array of string
	$category = $app->request->post('category');
	try{
		$db = connectDB();
		$sql = "select * from worker where tag LIKE '%$category%'";
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