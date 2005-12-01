<?php
defined( '_VALID_MOS' ) or die( 'Direct Access to this location is not allowed.' );
// needed to seperate the ISO number from the language file constant _ISO
$iso = explode( '=', _ISO );
// xml prolog
echo '<?xml version="1.0" encoding="'. $iso[1] .'"?' .'>';
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<?php
mosShowHead();
if ( $my->id ) { initEditor(); }
?>
<meta http-equiv="Content-Type" content="text/html; <?php echo _ISO; ?>" />
<link href="<?php echo $mosConfig_live_site;?>/templates/OSPI/css/template_css.css" rel="stylesheet" type="text/css" />
</head>

<body>

<!-- page -->
<div id="page">

<!-- login -->
<div id="login">
    <?php
    if ( mosCountModules( 'login' ) ) {
        mosLoadModules ( 'login' );
    } else {
        echo('<span class="error">Login Module Empty</span>');
    }
    ?>
</div>
<div align="right" class="clr"><img alt="" border="0" src="<?php echo $mosConfig_live_site;?>/templates/OSPI/images/login_bars.png" /></div>
<!-- /login -->

<!-- header -->
<div id="header_outer">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr valign="top">
        <td id="logo" rowspan="2">
            <a href="<?php echo $mosConfig_live_site;?>/" title="Open Source Portfolio Initiative | Home"><img alt="The Open Source Portfolio Initiative" border="0" height="55" width="127" src="<?php echo $mosConfig_live_site;?>/templates/OSPI/images/ospi_logo.gif" /></a>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr valign="top">
        <td id="header_inner">
            <?php
            if ( mosCountModules( 'header' ) ) {
                mosLoadModules ( 'header' );
            } else {
                echo('<span class="error">Header Module Empty</span>');
            }
            ?>
        </td>
    </tr>
</table>
</div>
<!-- /header -->

<!-- body -->
<div id="body_outer">
    <div id="body_inner">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" class="content_table">
            <tr valign="top">

                <!-- column left -->
                <td id="left_outer">
                    <div id="left_inner">
                        <?php
                        if ( mosCountModules( 'mainmenu' ) ) {
                            mosLoadModules ( 'mainmenu' );
                        } else {
                            echo('<span class="error">Mainmenu Module Empty</span>');
                        }
                        ?>
                        <div><img src="<?php echo $mosConfig_live_site;?>/templates/OSPI/images/mainmenu_footer.png" border="0" height="12" width="200" alt="" /></div>
                        <?php
                        if ( mosCountModules( 'left' ) ) {
                            mosLoadModules ( 'left' );
                        } else {
                            echo('<span class="error">Left Column Module Empty</span>');
                        }
                        ?>
                    </div>
                </td>
                <!-- /column left -->

                <!-- column middle -->
                <td>

                    <?php if ( mosCountModules ('banner') ) { ?>
                    <!-- banners -->
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" class="content_table">
                        <tr>
                            <td>
                                <div id="banner_inner"><?php mosLoadModules( 'banner', -1 ); ?><br /></div>
                            </td>
                        </tr>
                    </table>
                    <!-- /banners -->
                    <?php } ?>

                    <?php if ($option!="com_frontpage") { ?>
                    <!-- breadcrumb -->
                    <div id="pathway_outer">
                        <div id="pathway_inner">
                            <div id="pathway_text"><?php if ($option!="com_frontpage") mosPathWay(); ?></div>
                        </div>
                    </div>
                    <div class="clr"></div>
                    <!-- /breadcrumb -->
                    <?php } ?>

                    <!-- content -->
                    <?php mosMainBody(); ?>
                    <!-- /content -->

                </td>
                <!-- /column middle -->

                <?php if ( mosCountModules ('right') ) { ?>
                <!-- column right -->
                <td>
                    <div id="right_outer">
                        <div id="right_inner">
                        <?php mosLoadModules ( 'right' ); ?>
                        </div>
                    </div>
                </td>
                <!-- /column right -->
                <?php } ?>
                                
            </tr>
        </table>
    </div>
</div>
<!-- /body -->

<?php if ( mosCountModules ('debug') ) { ?>
<!-- footer -->
<?php mosLoadModules( 'debug', -1 ); ?>
<!-- /footer -->
<?php } ?>

</div>
<!-- /page -->

</body>
</html>
