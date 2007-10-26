#!/usr/bin/perl -w


#**************************************************************#
sub unit_expansion {
	
	if ( $type{ $curr }	eq	"UNIT" ) {
		
		my $unit	=	quotemeta( $ort{ $curr } );
		my $lc_unit = lc($unit);


		if ( exists ($unit_list{ $unit } )) {
			$exp{ $curr }			=	"$unit_list{ $unit}";
			$transcription{ $curr }		=	"-";
			
		} elsif ( exists ($unit_list{ $lc_unit } )) {

			$exp{ $curr }			=	"$unit_list{ $lc_unit}";
			$transcription{ $curr }		=	"-";
		}

	}
	
	

} # end sub
#**************************************************************#
1;
