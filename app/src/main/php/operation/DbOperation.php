<?php

/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 10-june-2018.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
class DbOperation
{
    protected $con;
    //$date_time = date('d-m-Y H:i:s');
     
    function __construct()
    {
        require_once  '../include/DbConnect.php';
        $db = new DbConnect();
        $this->con = $db->connect();
    }

	function get_result( $Statement ) {
		$RESULT = array();
		$Statement->store_result();
		for ( $i = 0; $i < $Statement->num_rows; $i++ ) {
			$Metadata = $Statement->result_metadata();
			$PARAMS = array();
			while ( $Field = $Metadata->fetch_field() ) {
				$PARAMS[] = &$RESULT[ $i ][ $Field->name ];
			}
			call_user_func_array( array( $Statement, 'bind_result' ), $PARAMS );
			$Statement->fetch();
		}
		//error_log(json_encode($RESULT));
		return $RESULT;
	}
}