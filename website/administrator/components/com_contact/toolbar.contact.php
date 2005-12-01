<?php
/**
* @version $Id: toolbar.contact.php,v 1.5 2005/01/06 01:13:16 eddieajau Exp $
* @package Mambo
* @subpackage Contact
* @copyright (C) 2000 - 2005 Miro International Pty Ltd
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL
* Mambo is Free Software
*/

/** ensure this file is being included by a parent file */
defined( '_VALID_MOS' ) or die( 'Direct Access to this location is not allowed.' );

require_once( $mainframe->getPath( 'toolbar_html' ) );

switch ( $task ) {
	case 'new':
	case 'edit':
	case 'editA':
		TOOLBAR_contact::_EDIT();
		break;

	default:
		TOOLBAR_contact::_DEFAULT();
		break;
}
?>