<?php
/**
* @version $Id: mod_login.php,v 1.5 2005/01/15 06:49:01 stingrey Exp $
* @package Mambo
* @copyright (C) 2000 - 2005 Miro International Pty Ltd
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL
* Mambo is Free Software
*/

/** ensure this file is being included by a parent file */
defined( '_VALID_MOS' ) or die( 'Direct Access to this location is not allowed.' );

$return = mosGetParam( $_SERVER, 'REQUEST_URI', null );
// converts & to &amp; for xtml compliance
$return = str_replace( '&', '&amp;', $return );

$registration_enabled 	= $mainframe->getCfg( 'allowUserRegistration' );
$message_login 			= $params->def( 'login_message', 0 );
$message_logout 		= $params->def( 'logout_message', 0 );
$pretext 	= $params->get( 'pretext' );
$posttext 	= $params->get( 'posttext' );
$login 		= $params->def( 'login', $return );
$logout 	= $params->def( 'logout', $return );
$name 		= $params->def( 'name', 1 );
$greeting 	= $params->def( 'greeting', 1 );

if ( $name ) {
	$query = "SELECT name FROM #__users WHERE id = ". $my->id;
	$database->setQuery( $query );
	$name = $database->loadResult();
} else {
	$name = $my->username;
}

if ( $my->id ) {
	?>
	<form action="<?php echo sefRelToAbs( 'index.php?option=logout' ); ?>" method="post" name="login" >
	<?php
	if ( $greeting ) {
                echo "<span class=\"arrow\">&gt;</span>";
		echo _HI;
		echo $name;
	}
	?>
	<input type="submit" name="Submit" class="button" value="<?php echo _BUTTON_LOGOUT; ?>" />
	<input type="hidden" name="op2" value="logout" />
	<input type="hidden" name="lang" value="<?php echo $mosConfig_lang; ?>" />
	<input type="hidden" name="return" value="<?php echo sefRelToAbs( $logout ); ?>" />
	<input type="hidden" name="message" value="<?php echo $message_logout; ?>" />
	</form>
	<?php
} else {
	?>
	<form action="<?php echo sefRelToAbs( 'index.php' ); ?>" method="post" name="login" >
	<?php
	echo $pretext;
	?>
	<?php
	if ( $registration_enabled ) {
		?>
			<span class="arrow">&gt;</span><a href="<?php echo sefRelToAbs( 'index.php?option=com_registration&amp;task=register' ); ?>"><?php echo _CREATE_ACCOUNT; ?></a>
		<?php
	}
	?>
		<span class="arrow">&gt;</span><a href="<?php echo sefRelToAbs( 'index.php?option=com_registration&amp;task=lostPassword' ); ?>"><?php echo _LOST_PASSWORD; ?></a>
		<input type="checkbox" name="remember" id="remember" value="yes" alt="Remember Me" /> 
		<label for="remember"><?php echo _REMEMBER_ME; ?></label>
		<label for="username"><span class="arrow">&gt;</span><?php echo _USERNAME; ?></label>
		<input name="username" id="username" type="text" class="inputbox" alt="username" size="10" />
		<label for="passwd"><?php echo _PASSWORD; ?></label>
		<input type="password" name="passwd" id="passwd" class="inputbox" size="10" alt="password" />
		<input type="submit" name="Submit" class="button" value="<?php echo _BUTTON_LOGIN; ?>" />
		<input type="hidden" name="option" value="login" />
	<?php
	echo $posttext;
	?>
	<input type="hidden" name="op2" value="login" />
	<input type="hidden" name="lang" value="<?php echo $mosConfig_lang; ?>" />
	<input type="hidden" name="return" value="<?php echo sefRelToAbs( $login ); ?>" />
	<input type="hidden" name="message" value="<?php echo $message_login; ?>" />
	</form>
	<?php
}
?>
