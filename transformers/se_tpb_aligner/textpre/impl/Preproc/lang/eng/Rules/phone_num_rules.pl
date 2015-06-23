#!/usr/bin/perl -w


#**************************************************************#
sub phone_num_subs {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&phone_num_1();
	&phone_num_2();
	&phone_num_3();

#	$text =~ s/\b((?:08|0[1-9]?\d?\d?)-[ \d]+)([^-]|$)/<PHONE>$1<\/PHONE>$2/g) {

}
#**************************************************************#
#
# Example:	Tel. 08-444 44 44
#
#**************************************************************#
sub phone_num_1 {



	if (exists ($ort{ $prev_3 } )) {

#		print "\n-----------
#		P3: $ort{$prev_3}
#		P2: $ort{$prev_2}
#		P1: $ort{$prev_1}
#		CURR: $ort{$curr}
#		\n";
	
		if (	
			# Telefon: 08-690 90 90
			# Context
			$ort{ $prev_3 }	=~	/^$phone_num$/i
			&&
			$ort{ $prev_2 }	=~	/^[\.\:]$/
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $curr }	=~	/^\d+$/
		) {
			
			# Retag
			$type{ $prev_3 }	=	"PHONE";
			$type{ $prev_2 }	=	"PHONE";
			$type{ $prev_1 }	=	"PHONE";
			$type{ $curr }		=	"PHONE";

			&phone_rc($curr);
		
		}

	} # end exists
} # end sub
#**************************************************************#
sub phone_num_2 {

	if (exists ($ort{ $prev_2 } )) {

#		print "\n-----------
#		P2: $ort{$prev_2}
#		P1: $ort{$prev_1}
#		CURR: $ort{$curr}
#		\n";
	
		if (	
			# Telefon:08-690 90 90
			# Context
			$ort{ $prev_2 }	=~	/^$phone_num$/i
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $curr }	=~	/^\d+$/
		) {
			
			# Retag
			$type{ $prev_2 }	=	"PHONE";
			$type{ $prev_1 }	=	"PHONE";
			$type{ $curr }		=	"PHONE";

			&phone_rc($curr);
		}

	} # end exists
} # end sub
#**************************************************************#
sub phone_num_3 {

	if (exists ($ort{ $prev_1 } )) {

#		print "\n-----------
#		P1: $ort{$prev_1}
#		CURR: $ort{$curr}
#		\n";
	
		if (	
			# Tel.nr.08-690 90 90
			# Context
			$ort{ $prev_1 }	=~	/^$phone_num$/i
			&&
			$ort{ $curr }	=~	/^\d+$/
		) {
			# Retag
			$type{ $prev_1 }	=	"PHONE";
			$type{ $curr }		=	"PHONE";

			&phone_rc($curr);

		}

	} # end exists
} # end sub
#**************************************************************#
# phone_rc
#
# Spread PHONE-type until phone number ends.
#
#**************************************************************#
sub phone_rc {
	
	my $curr	= 	shift;	
	my $retag	=	0;
	my $counter	=	0;
			
			
	# Continue until phone number ends.
	foreach my $field (sort (keys (%ort ) ))  {
				
		my $next_field	=	$counter + 1;
		$next_field	=	&format_pcounter( $next_field );
		
#		print "FIELD: $field\tNEXT: $next_field\n";
			
#		print "FIELD: $ort{ $field }\nNEXT: $ort{ $next_field }\n";
		
		if ( $field	eq	$curr ) {
			$retag	= 1;
		}
		
		if (
			$retag		==	1
			&&
			$ort{ $field }	=~	/^\d+$/
			|| (
				$ort{ $field }	=~	/^[\(\)\/\-]$/
				&&
				$ort{ $next_field }	=~	/^\d+$/
			) || (
				$pos{ $field }	eq	"DEL"
				&&
				$ort{ $next_field }	=~	/^\d+$/
			)
			
		) {
			# Retag
			$type{ $field }	= 	"PHONE";
			
				
		# Stop PHONE-tagging
		} else {
			$retag		=	0;
		}
				
#		print "RETAG: $retag\n\n";
		
		$counter++;

	} # end foreach
			
} # end sub
#**************************************************************#
1;
