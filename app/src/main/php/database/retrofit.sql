-- phpMyAdmin SQL Dump
-- version 4.8.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 06, 2018 at 05:45 AM
-- Server version: 10.1.32-MariaDB
-- PHP Version: 7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `retrofit`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `email` varchar(200) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `password` text NOT NULL,
  `dob` varchar(30) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `address` text NOT NULL,
  `image` text NOT NULL,
  `api_key` text NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `mobile`, `password`, `dob`, `gender`, `address`, `image`, `api_key`, `created_date`) VALUES
(1, 'Santosh', 'santosh@gmail.com', '1234567890', '7e38ed6b699c58f633194b31c1fdba8275314dabfd3838e3599565869099ad8c', '12/04/1992', 'Male', 'jdgfjhsf', '0a9f0c97ce8f00673a6d59a37dec93f7.jpg', '1050179ba0ddd7a31503c01e0a3d88e3', '2018-09-05 04:17:41'),
(2, 'jksdhfjkhsjkgc', 'santosh1@gmail.com', '1234556789', 'c895d06c0ff460df575c777678294368e38e033f224698c77017a286a7295001', '', '', '', '', 'c491bcfc222fb29fcf2500323246ac26', '2018-09-06 03:31:01'),
(3, 'santosh', 'santoshkumar@gmail.com', '0987654321', '5641e9011b65e536b341b51735397119f9b9528899dc156cc80034e7319248bf', '', '', '', '', 'a0518ecaad4f3dbb68ade05388d68e00', '2018-09-06 03:34:33');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
