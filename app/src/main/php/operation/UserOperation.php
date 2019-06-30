<?php
/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 10-june-2018.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
    use PHPMailer\PHPMailer\PHPMailer;
    use PHPMailer\PHPMailer\Exception;
    require '../PHPMailer/src/Exception.php';
    require '../PHPMailer/src/PHPMailer.php';
    require '../PHPMailer/src/SMTP.php';
class UserOperation extends DbOperation
{
    //Method to register a new User
    public function register($name, $email, $password, $mobile)
    {
        date_default_timezone_set('Asia/Kolkata');
        $dateTime = date('d-m-Y H:i:s');

        if (!$this->isUserExists($email, $mobile)) {
            $apikey = $this->generateApiKey();
            $password = hash('sha256', $apikey.$password);
            $stmt = $this->con->prepare("INSERT INTO users(name, email, password, mobile, api_key) values(?,?,?,?,?)");
            $stmt->bind_param("sssss", $name, $email, $password, $mobile, $apikey);
            $result = $stmt->execute();
            $stmt->close();
            if ($result) {
                return 0;
            } else {
                echo $stmt->error;
                return 1;
            }
        } else {
            return 2;
        }
    }

   

    //Method to let a User log in
    public function userLogin($email, $pass)
    {
        $apikey = $this->getApiKey($email);
		//error_log("API key->".$apikey);
        $password = hash('sha256', $apikey.$pass);
		//error_log("Password->".$password);
		//$user = $this->getUser($email);
		//error_log("Password from DB ->".$user['password']);
        $stmt = $this->con->prepare("SELECT id from users WHERE email = '$email' OR password = '$password'");
        $stmt->execute();
		$users = $this->get_result($stmt);
        $stmt->close();
        if(count($users) > 0){
			return true;
		}
		else {
			return false;
		}
    }

    //Method to get User details
    public function getUser($email)
    {
        $stmt = $this->con->prepare("SELECT * FROM users WHERE email = ? OR mobile = ?");
        $stmt->bind_param("ss", $email, $email);
        $stmt->execute();
        $users = $this->get_result($stmt);
        $stmt->close();
		if(count($users) > 0){
			return $users[0];
		}
		else {
			return null;
		}
     }

    //Method to get User details
    public function getUserDetails($id)
    {
        $stmt = $this->con->prepare("SELECT * FROM users WHERE id = ?");
        $stmt->bind_param("s", $id);
        $stmt->execute();
        $users = $this->get_result($stmt);
        $stmt->close();
        		if(count($users) > 0){
			return $users[0];
		}
		else {
			return null;
		}
    }
   
    public function getProfileImage($userId)
    {
        $stmt = $this->con->prepare("SELECT image FROM users WHERE id = '$userId' ");
        if ($stmt->execute()) {
            $stmt->bind_result($image);
            $stmt->fetch();
            $stmt->close();
            return $image;
        } else {
            return null;
        }
    }
   
    
    public function updateUsers($id, $gender,$dob,$address)
    {
        $stmt = $this->con->prepare("UPDATE users SET gender = ?, dob = ?, address = ? WHERE id = ?");
        $stmt->bind_param("ssss", $gender, $dob, $address, $id);
        if ($stmt->execute()) {
            return 0;
        } else {
            return 1;
        }

    }

    public function updateImage($id, $imageURL)
    {
        $stmt = $this->con->prepare("UPDATE users SET image = ? WHERE id = ?");
        $stmt->bind_param("ss", $imageURL,$id);
        if ($stmt->execute()) {
            return 0;
        } else {
            return 1;
        }

    }

    public function deleteUser($id)
    {
        $stmt = $this->con->prepare("DELETE FROM users  WHERE id = ?");
        $stmt->bind_param("i", $id);
        if ($stmt->execute()) {
            return 0;
        } else {
            return 1;
        }

    }

    //Method to fetch all users from database
    public function getAllusers()
    {
        $stmt = $this->conn->prepare("SELECT * FROM users");
        $stmt->execute();
        $users = $this->get_result($stmt);
        $stmt->close();
        return $users;
    }

    //Method to check the User email already exist or not
    public function isUserExists($email, $mobile)
    {
        $stmt = $this->con->prepare("SELECT id from users WHERE email = ? OR mobile = '$mobile'");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    //Checking the User is valid or not by api key
    public function isValidUser($api_key)
    {
        $stmt = $this->con->prepare("SELECT id from users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    /**
     * Fetching user id by api key
     * @param String $api_key user api key
     */
    public function getUserId($api_key) {
        $stmt = $this->con->prepare("SELECT id FROM users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        if ($stmt->execute()) {
            $stmt->bind_result($user_id);
            $stmt->fetch();
            $stmt->close();
            return $user_id;
        } else {
            return NULL;
        }
    }

    /**
     * Fetching user api key by email;
     * @param String $api_key user api key
     */
    public function getApiKey($email)
    {
        $stmt = $this->con->prepare("SELECT api_key FROM users WHERE email = '$email' OR mobile = '$email'");
        //$stmt->bind_param("ss", $email, $email);
        if ($stmt->execute()) {
            $stmt->bind_result($api_key);
            $stmt->fetch();
            $stmt->close();
            return $api_key;
        } else {
            return null;
        }
    }

  
 public function SendMailViaSmtp($to, $name)
    {
      $mail = new PHPMailer(true);  
      
      try {
    //Server settings
    $mail->SMTPDebug = 2;                                
    $mail->isSMTP();                                 
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;                              
    $mail->Username = 'test@gmail.com';                
    $mail->Password = 'test123';                           
    $mail->SMTPSecure = 'tls';                           
    $mail->Port = 587;  
    $mail->setFrom('info@domain.com','Test');
    $mail->addAddress($to, $name);     // Add a recipient
    
    $mail->isHTML(true);                                  // Set email format to HTML
    $mail->Subject = 'Resister Successful';
    $mail->Body    = 'Thank u for Registering with us';
    
      if($mail->Send()) { 
            $error = 'Message sent!';
            return 0;
        } else  {
            $error = 'Mail error: '.$mail->ErrorInfo;
            return 1;
        }

      }catch (Exception $e) {
    echo 'Message could not be sent. Mailer Error: ', $mail->ErrorInfo;
     }

  }



    //Method to generate a unique api key every time
    private function generateApiKey()
    {
        return md5(uniqid(rand(), true));
    }

}