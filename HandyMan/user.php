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
$app->post('/getallworker','getAllWorker');
$app->post('/voteworker','voteWorker');
$app->post('/history','getHistory');
$app->post('/updateprofile','updateProfile');
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
function updateProfile(){
	$app = \Slim\Slim::getInstance();
	try{
		$db = connectDB();
		$username = $app->request->post('username');
		$phone = $app->request->post('phone');
		$address = $app->request->post('address');
		$password = sha1($app->request->post('password'));
		$new_password = $app->request->post('new_password');
		$legal_check_sql = "SELECT * FROM user WHERE username='$username' AND password='$password'";
		$result = $db->query($legal_check_sql);
		$fetch = $result->fetch(PDO::FETCH_ASSOC);
		if (empty($fetch)) {
			echo "Wrong username or password";
		} else {
			if (strcmp($new_password, "") == 0 ) {
				if (strcmp($address, "") !== 0 and strcmp($phone, "") == 0) {
					$update_sql = "UPDATE user SET address='$address' WHERE username='$username'";
					$db->query($update_sql);
				} else if(strcmp($address, "") == 0 and strcmp($phone, "") !== 0){
					$update_sql = "UPDATE user SET phone='$phone' WHERE username='$username'";
					$db->query($update_sql);
				}else{
					$update_sql = "UPDATE user SET phone='$phone',address='$address' WHERE username='$username'";
					$db->query($update_sql);
				}	
			}
			else{
				$new_password = sha1($new_password);
				if (strcmp($address, "") !== 0 and strcmp($phone, "") == 0) {
					$update_sql = "UPDATE user SET address='$address',password='$new_password' WHERE username='$username'";
					$db->query($update_sql);
				} else if(strcmp($address, "") == 0 and strcmp($phone, "") !== 0){
					$update_sql = "UPDATE user SET phone='$phone',password='$new_password' WHERE username='$username'";
					$db->query($update_sql);
				}else{
					$update_sql = "UPDATE user SET phone='$phone',address='$address',password='$new_password' WHERE username='$username'";
					$db->query($update_sql);
				}	
			}
			
		}
		
	}
	catch (Exception $e){
		echo $e;
	}
}
function voteWorker(){
	$app = \Slim\Slim::getInstance();
	try{
		$db = connectDB();
		$worker_username = $app->request->post('worker_username');
		$user_username = $app->request->post('user_username');
		$vote = intval($app->request->post('vote'));
		//to get new ratings, the formula is as folows:
		//(rating_before*total_voters_before + vote_now) / (total_voters_before+1)
		$worker_rating_sql = "SELECT rating FROM worker WHERE username='$worker_username'";
		$rating_sql_result = $db->query($worker_rating_sql);
		$worker_rating = $rating_sql_result->fetch(PDO::FETCH_ASSOC);
		$rating = floatval($worker_rating['rating']);
		$worker_vote_history_sql = "SELECT COUNT(*) AS total FROM user_has_rated WHERE worker_username='$worker_username'";
		$count = $db->query($worker_vote_history_sql);
		$fetch = $count->fetch(PDO::FETCH_ASSOC);
		$total = intval($fetch['total']);
		$new_rating = floatval((($rating*$total) + $vote)/($total+1));
		echo "hehe";
		$update_sql = "UPDATE worker SET rating='$new_rating' WHERE username='$worker_username'";
		$db->query($update_sql);
		$insert_sql = "INSERT INTO user_has_rated (user_username,worker_username) VALUES ('$user_username', '$worker_username')";
		$db->query($insert_sql);
	}
	catch (Exception $e){
		echo "Something Wrong";
	}
}

function getHistory(){
	$app = \Slim\Slim::getInstance();
	$order_list = array();
	try{
		$db = connectDB();
		$username = $app->request->post('username');
		$sql = "SELECT * FROM user_order WHERE user_username='$username' AND order_status='1'";
		$order = $db->query($sql);
		$fetch_order = $order->fetchAll(PDO::FETCH_ASSOC);
		foreach ($fetch_order as $row) {
			$id = $row['id'];
			$sql_getWorker = "SELECT name FROM worker WHERE username=(SELECT worker_username FROM worker_order WHERE user_order_id='$id')";
			$getWorker = $db->query($sql_getWorker);
			$fetch_getWorker = $getWorker->fetch(PDO::FETCH_OBJ);
			array_push($order_list, 
				array(
						'worker' => $fetch_getWorker->name,
						'date' => $row['date'],
						'category'=>$row['category'],
						'address'=>$row['address']
					)
				);
		}
		$result = json_encode($order_list);
		echo $result;
	}catch (Exception $e){
		echo "Something Wrong";
	}
	
}
function getAllWorker(){
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
		foreach ($fetch_order as $row) {
			//get_all_worker in some order
			$id = $row['id'];
			$worker_sql = "SELECT worker_username FROM worker_order WHERE user_order_id='$id'";
			$worker = $db->query($worker_sql);
			$fetch_worker = $worker->fetchAll(PDO::FETCH_ASSOC);
			foreach ($fetch_worker as $my_worker) {
				# get all worker username that user
				if(!in_array($worker_list,$my_worker)){
					array_push($worker_list, $my_worker);
					$worker_name = $my_worker['worker_username'];
					$worker_data_sql = "SELECT * FROM worker WHERE username='$worker_name'";
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
//given username get all worker not voted by that user
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
		foreach ($fetch_order as $row) {
			//get_all_worker in some order
			$id = $row['id'];
			$worker_sql = "SELECT worker_username FROM worker_order WHERE user_order_id='$id' AND worker_username NOT IN (SELECT user_has_rated.worker_username FROM user_has_rated WHERE user_username='$username');";
			$worker = $db->query($worker_sql);
			$fetch_worker = $worker->fetchAll(PDO::FETCH_ASSOC);
			foreach ($fetch_worker as $my_worker) {
				# get all worker username that user
				if(!in_array($worker_list,$my_worker)){
					array_push($worker_list, $my_worker);
					$worker_name = $my_worker['worker_username'];
					$worker_data_sql = "SELECT * FROM worker WHERE username='$worker_name'";
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
		$dsn = 'mysql:dbname=handyman;host=localhost';
		$dbuser = 'ppla05';
		$password = 'ppla05';	
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