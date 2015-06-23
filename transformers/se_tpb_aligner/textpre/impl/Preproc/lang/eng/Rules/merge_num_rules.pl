#!/usr/bin/perl -w


#**************************************************************#
sub merge_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&merge_num_comma_4();
	&merge_num_comma_3();
	&merge_num_comma_2();

	&merge_num_4();
	&merge_num_3();
	&merge_num_2();

}

#**************************************************************#
# 4 digit chunks with comma separation
# Example:	52,500,000,000	-->	52500000000
#**************************************************************#
sub merge_num_comma_4 {

	if (exists ($ort{ $next_6 } )) {

		if (
			# Context
			$ort{ $curr }	=~	/^\d\d?\d?$/
			&&
			$ort{ $next_1 }	eq	","
			&&
			$ort{ $next_2 }	=~	/^\d\d\d$/
			&&
			$ort{ $next_3 }	eq	","
			&&
			$ort{ $next_4 }	=~	/^\d\d\d$/
			&&
			$ort{ $next_5 }	eq	","
			&&
			$ort{ $next_6 }	=~	/^\d\d\d$/

		) {
			
			# Merge fields
			$ort{ $curr }	=	$ort{ $curr } . $ort{ $next_1 } . $ort{ $next_2 } . $ort{ $next_3 } . $ort{ $next_4 } . $ort{ $next_5 } . $ort{ $next_6 };
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );
			delete( $ort{ $next_3 } );
			delete( $ort{ $next_4 } );
			delete( $ort{ $next_5 } );
			delete( $ort{ $next_6 } );

			# Retag
			# $pos{ $curr }	=	"NUM CARD";
			
			# Renumber fields
			&renumber_fields();			
			
		}
	} # end exists		
} # end sub
#**************************************************************#
# 3 digit chunks with comma separation
# Example:	52,500,000	-->	52500000
#**************************************************************#
sub merge_num_comma_3 {

	if (exists ($ort{ $next_4 } )) {

		if (
			# Context
			$ort{ $curr }	=~	/^\d\d?\d?$/
			&&
			$ort{ $next_1 }	eq	","
			&&
			$ort{ $next_2 }	=~	/^\d\d\d$/
			&&
			$ort{ $next_3 }	eq	","
			&&
			$ort{ $next_4 }	=~	/^\d\d\d$/

		) {
			
			# Merge fields
			$ort{ $curr }	=	$ort{ $curr } . $ort{ $next_1 } . $ort{ $next_2 } . $ort{ $next_3 } . $ort{ $next_4 };
			
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );
			delete( $ort{ $next_3 } );
			delete( $ort{ $next_4 } );

			# Retag
			# $pos{ $curr }	=	"NUM CARD";

			# Renumber fields
			&renumber_fields();			
		}
	} # end exists		
} # end sub
#**************************************************************#
# 2 digit chunks with comma separation
# Example:	52,500	-->	52500
#**************************************************************#
sub merge_num_comma_2 {


	if (exists ($ort{ $next_2 } )) {

		if (
			# Context
			$ort{ $curr }	=~	/^\d\d?\d?$/
			&&
			$ort{ $next_1 }	eq	","
			&&
			$ort{ $next_2 }	=~	/^\d\d\d$/

		) {
			
			# Merge fields
			$ort{ $curr }	=	$ort{ $curr } . $ort{ $next_1 } . $ort{ $next_2 };
			
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );

			# Retag
			# $pos{ $curr }	=	"NUM CARD";

			# Renumber fields
			&renumber_fields();			
		}
	} # end exists		
} # end sub
#**************************************************************#
# 4 digit chunks with space separation
# Example:	52 500 000 000	-->	52 500 000 000
#**************************************************************#
sub merge_num_4 {

	if (exists ($ort{ $next_6 } )) {

		if (
			# Context
			$ort{ $curr }	=~	/^\d\d?\d?$/
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^\d\d\d$/
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^\d\d\d$/
			&&
			$pos{ $next_5 }	eq	"DEL"
			&&
			$ort{ $next_6 }	=~	/^\d\d\d$/

		) {
			
			# Merge fields
			$ort{ $curr }	=	$ort{ $curr } . $ort{ $next_1 } . $ort{ $next_2 } . $ort{ $next_3 } . $ort{ $next_4 } . $ort{ $next_5 } . $ort{ $next_6 };
			
#			print "OC $ort{$curr}\n\n";
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );
			delete( $ort{ $next_3 } );
			delete( $ort{ $next_4 } );
			delete( $ort{ $next_5 } );
			delete( $ort{ $next_6 } );

			# Retag
			# $pos{ $curr }	=	"NUM CARD";

			# Renumber fields
			&renumber_fields();			
		}
	} # end exists		
} # end sub
#**************************************************************#
# 3 digit chunks with space separation
# Example:	52 500 000	-->	52 500 000
#**************************************************************#
sub merge_num_3 {

	if (exists ($ort{ $next_4 } )) {

		if (
			# Context
			$ort{ $curr }	=~	/^\d\d?\d?$/
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^\d\d\d$/
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^\d\d\d$/

		) {
			
			# Merge fields
			$ort{ $curr }	=	$ort{ $curr } . $ort{ $next_1 } . $ort{ $next_2 } . $ort{ $next_3 } . $ort{ $next_4 };
			
#			print "OC $ort{$curr}\n\n";
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );
			delete( $ort{ $next_3 } );
			delete( $ort{ $next_4 } );
			delete( $ort{ $next_5 } );
			delete( $ort{ $next_6 } );

			# Retag
			# $pos{ $curr }	=	"NUM CARD";

			# Renumber fields
			&renumber_fields();			
		}
	} # end exists		
} # end sub
#**************************************************************#
# 2 digit chunks with space separation
# Example:	52 500	-->	52 500
#**************************************************************#
sub merge_num_2 {


	if (exists ($ort{ $next_2 } )) {


		if (
			# Context
			$ort{ $curr }	=~	/^\d\d?\d?$/
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^\d\d\d$/

		) {
			
#			print "\n\nO $ort{$curr}\t$pos{$curr}\n";
#			print "O $ort{$next_1}\t$pos{$next_1}\n";
#			print "O $ort{$next_2}\t$pos{$next_2}\n\n";

			# Merge fields
			$ort{ $curr }	=	$ort{ $curr } . $ort{ $next_1 } . $ort{ $next_2 };
			
#			print "OC $ort{$curr}\n\n";
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );

#			foreach $k (sort(keys(%ort))) {
#				print "JOJOJO $k\t$ort{$k}\n";
#			}

			# Retag
			$pos{ $curr }	=	"NUM CARD";

			# Renumber fields
			&renumber_fields();			

#			print OUTPUT "\n\nJJJJJJJJJJJJJJ\n\n";
#			&print_all_output();

		}
	} # end exists		
} # end sub
#**************************************************************#

1;