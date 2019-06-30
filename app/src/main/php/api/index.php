<?php

/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 10-june-2018.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */

require '.././libs/Slim/Slim.php';

require '../operation/DbOperation.php';
require '../operation/UserOperation.php';

 /*HTTP status codes
200  OK
201 Created
304 Not Modified
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
422 Unprocessable Entity
500 Internal Server Error
 */
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();

// Register routes for User

// User id from db - Global Variable
$user_id = NULL;


$app->post('/users/register', 'registerUser');
$app->post('/users/login', 'loginUser');
$app->post('/users/uploadimage', 'authenticateUser', 'uploadImage');
$app->put('/users/:id', 'authenticateUser', 'updateUsers');


/* *
 * URL: http://localhost/slimapi/api/users/register
 * Parameters: name, email, mobile, password
 * Method: POST
 * */
function registerUser()
{
    verifyRequiredParams(array('name', 'email', 'mobile', 'password'));
    $app = \Slim\Slim::getInstance();
    $jsonData = $app->request->getBody();
    $inputData = json_decode($jsonData);
    $name = $inputData->{'name'};
    $email = $inputData->{'email'};
    $mobile = $inputData->{'mobile'};
    $password = $inputData->{'password'};
   

    $response = array();
    $opUser = new UserOperation();
    $res = $opUser->register($name, $email, $password, $mobile);
    if ($res == 0) {
        $response["error"] = false;
        $response["message"] = "You are successfully registered";
        echoResponse(201, $response);
       // $opUser->SendMailViaSmtp($email, $name);
    } else if ($res == 1) {
        $response["error"] = true;
        $response["message"] = "Oops! An error occurred while registereing";
        echoResponse(200, $response);
    } else if ($res == 2) {
        $response["error"] = true;
        $response["message"] = "Sorry, this user  already existed";
        echoResponse(200, $response);
    }
    $opUser = null;
}

/* *
 * URL: http://localhost/slim2/api/users/login
 * Parameters: email, password
 * Method: POST
 * */
function loginUser()
{
    verifyRequiredParams(array('email','password'));
    $app = \Slim\Slim::getInstance();
    $req = $app->request;
   // $email = $req->headers('PHP_AUTH_USER');
    //$password = $req->headers('PHP_AUTH_PW');
    $jsonData = $app->request->getBody();
    $inputData = json_decode($jsonData);
    $email = $inputData->{'email'};
    $password = $inputData->{'password'};
    $opUser = new UserOperation();
    $response = array();
    $response['error'] = true;
    $response['message'] = "Invalid email or password";
    if ($opUser->userLogin($email, $password)) {
        $response['error'] = false;
        $response['message'] = "Login Successfull !!";
        $response['user'] = $opUser->getUser($email);
    }
    $opUser = NULL;
    echoResponse(200, $response);
}

/* *
 * URL: http://localhost/slimapi/api/users/uploadimage
 * Parameters: image(file)
 * Method: POST
 * */
function uploadImage(){
    $app = \Slim\Slim::getInstance();
       $target_dir = "../uploads/profile/";
       global $user_id;
       $response = array();
       $filename = md5(random_bytes(35));
       $temppath = ($_FILES['image']['tmp_name']);
       $imageURL =  $filename .".jpg";
          
        if(move_uploaded_file($temppath, $target_dir . $filename .".jpg")){
           $opUser = new UserOperation();
           $image = $opUser->getProfileImage($user_id);
            $res = $opUser->updateImage($user_id, $imageURL);
           if ($res == 0) {
            if($image!=null){
             unlink("../uploads/profile/".$image);
             }
            $response["error"] = false;
            $response["message"] = "Image uploaded Successfully";
            $response['user'] = $opUser->getUserDetails($user_id);
            echoResponse(201, $response);
          } else if ($res == 1) {
              $response["error"] = true;
              $response["message"] = "Oops! An error occurred while uploading";
              echoResponse(400, $response);
           } 
      }
}

/* *
 * URL: http://localhost/slimapi/api/users/user_details
 * Parameters: dob, gender, address
 * Method: PUT
 * */
function updateUsers()
{
    verifyRequiredParams(array('dob', 'gender'));
    $app = \Slim\Slim::getInstance();
    $jsonData = $app->request->getBody();
    $inputData = json_decode($jsonData);
    $dob = $inputData->{'dob'};
    $gender = $inputData->{'gender'};
    $address = $inputData->{'address'};
    global $user_id;

    $response = array();
    $opUser = new UserOperation();
    $res = $opUser->updateUsers($user_id, $gender, $dob, $address);
    if ($res == 0) {
        $response["error"] = false;
        $response["message"] = "You are successfully Updated";
        $response['user'] = $opUser->getUserDetails($user_id);
        echoResponse(200, $response);
       // $opUser->SendMailViaSmtp($email, $name);
    } else if ($res == 1) {
        $response["error"] = true;
        $response["message"] = "Oops! An error occurred while Updating";
        echoResponse(200, $response);
    } 
    $opUser = null;
}





/**
 * Adding Middle Layer to authenticate every request
 * Checking if the request has valid api key in the 'Authorization' header
 */
function authenticate(\Slim\Route $route) {
    // Getting request headers
    $headers = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();

    // Verifying Authorization Header
    if (isset($headers['Authorization'])) {
        $db = new DbHandler();

        // get the api key
        $api_key = $headers['Authorization'];
        // validating api key
        if (!$db->isValidApiKey($api_key)) {
            // api key is not present in users table
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Api key";
            echoRespnse(401, $response);
            $app->stop();
        } else {
            global $user_id;
            // get user primary key id
            $user_id = $db->getUserId($api_key);
        }
    } else {
        // api key is missing in header
        $response["error"] = true;
        $response["message"] = "Api key is misssing";
        echoRespnse(400, $response);
        $app->stop();
    }
}


/**
 * Validating email address
 */
function validateEmail($email) {
    $app = \Slim\Slim::getInstance();
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["error"] = true;
        $response["message"] = 'Email address is not valid';
        echoRespnse(400, $response);
        $app->stop();
    }
}

function echoResponse($statusCode, $response)
{
    $app = \Slim\Slim::getInstance();
    $app->status($statusCode);
    $app->contentType('application/json');
    echo json_encode($response);
}


function verifyRequiredParams($required_fields)
{
    $error = false;
    $error_fields = "";

    $app = \Slim\Slim::getInstance();
    $jsonData = $app->request->getBody();
    $inputData = json_decode($jsonData);

    foreach ($required_fields as $field) {
        if (strlen(trim($inputData->{$field})) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }

    if ($error) {
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoResponse(400, $response);
        $app->stop();
    }
}


function authenticateuser(\Slim\Route $route)
{
    $headers = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();
    if (isset($headers['Authorization'])) {
        $opUser = new UserOperation();
        $api_key = $headers['Authorization'];
        if (!$opUser->isValiduser($api_key)) {
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Api key";
            echoResponse(401, $response);
            $app->stop();
        } else {
            global $user_id;
            // get user primary key id
            $user_id = $opUser->getUserId($api_key);
        }
    } else {
        $response["error"] = true;
        $response["message"] = "Api key is misssing";
        echoResponse(400, $response);
        $app->stop();
    }
}



$app->run();
?>