#!/usr/bin/perl -w

#**************************************************************#
sub ordinals_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

#	&ordinals_num_1();
	&ordinals_num_2();
	&ordinals_num_3();

}
#**************************************************************#
# ordinals_num_1
#
# Intervals with ordinals
# Example:	3-4§§
#		
#**************************************************************#
sub ordinals_num_1 {

#	print "\n
#	------------
#	ordinals_num_1
#	Curr:	$ort{ $curr }	
#	Next1:	$pos{ $next_1 }
#	Next2:	$ort{ $next_2 }	
#	\n";


	# With blanks
	if ( exists ( $ort{ $prev_2 } ) && exists ( $ort{ $next_2 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/^\d$/
			&&
			$ort{ $prev_1 }		eq	"-"
			&&
			$ort{ $prev_2 }		=~	/^\d$/
			
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^$ordinal_words$/io
		) {
			# Retag
			$type{ $prev_2 }	=	"NUM ORD INTERVAL";
			$type{ $prev_1 }	=	"NUM ORD INTERVAL";
			$type{ $curr }		=	"NUM ORD INTERVAL";

			$pos{ $prev_2 }		=	"NUM ORD";
			$pos{ $curr }		=	"NUM ORD";
			
		}
	} # end exists

	# Without blanks
	if ( exists ( $ort{ $prev_2 } ) && exists ( $ort{ $next_1 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/^\d$/
			&&
			$ort{ $prev_1 }		eq	"-"
			&&
			$ort{ $prev_2 }		=~	/^\d$/
			&&
			$ort{ $next_1 }		=~	/^$ordinal_words$/io
		) {
			# Retag
			$type{ $prev_2 }	=	"NUM ORD INTERVAL";
			$type{ $prev_1 }	=	"NUM ORD INTERVAL";
			$type{ $curr }		=	"NUM ORD INTERVAL";

			$pos{ $prev_2 }		=	"NUM ORD";
			$pos{ $curr }		=	"NUM ORD";
			
		}
	} # end exists
}
#**************************************************************#
# ordinals_num_2
#
# Numbers with ordinal endings preceeding ordinal words.
# Example:	2nd, 8th
#		
#**************************************************************#
sub ordinals_num_2 {

	if ( exists ( $ort{ $next_2 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/\d/
			&&
			$ort{ $next_1 }		=~	/^[\:\']$/
			&&
			$ort{ $next_2 }		=~	/^$ordinals_endings$/io
		) {
			# Retag
			$type{ $curr }		=	"NUM ORD";
			$type{ $next_1 }	=	"NUM ORD";
			$type{ $next_2 }	=	"NUM ORD";

			$pos{ $curr }		=	"NUM ORD";
			$pos{ $next_1 }		=	"NUM ORD";
			$pos{ $next_2 }		=	"NUM ORD";
			
		}
	} # end exists
	
	if ( exists ( $ort{ $next_1 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/\d/
			&&
			$ort{ $next_1 }		=~	/^$ordinals_endings$/io
		) {
			# Retag
			$type{ $curr }		=	"NUM ORD";
			$type{ $next_1 }	=	"NUM ORD";

			$pos{ $curr }		=	"NUM ORD";
			$pos{ $next_1 }		=	"NUM ORD";
			
		}
	} # end exists
}
#**************************************************************#
# ordinals_num_3
#
# Numbers preceeding ordinal words.
# Example:	2 chapt.
#
#**************************************************************#
sub ordinals_num_3 {

#	print "\n
#	------------
#	ordinals_num_1
#	Curr:	$ort{ $curr }	
#	Next1:	$pos{ $next_1 }
#	Next2:	$ort{ $next_2 }	
#	\n";


	# With blanks
	if ( exists ( $ort{ $next_2 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/\d/
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^$ordinal_words$/io
		) {


			# Retag
			$type{ $curr }		=	"NUM ORD";

			$pos{ $curr }		=	"NUM ORD";
			
		}
	} # end exists

	# Without blanks
	if ( exists ( $ort{ $next_1 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/\d/
			&&
			$ort{ $next_1 }		=~	/^$ordinal_words$/io
		) {
			# Retag
			$type{ $curr }		=	"NUM ORD";

			$pos{ $curr }		=	"NUM ORD";
			
		}
	} # end exists
}
#**************************************************************#
1;
#**************************************************************#

#	# 061128
#	# Om det finns tal före ett ordningstal, separarerat av "," eller "och", gör även dem till ordningstal.
#	# 070112 Tar bort att mellanslag gills (9 6 Â 2 st jämställdhetslagen.)
#	if ($text =~ /(\d+(?:,\d|[,\-]|och)+) *<NUM_ORDI>/) {
#		
#		my $ordinals1 = $1;
#
#		my $ordinals2 = $ordinals1;
#		
#		$ordinals2 =~ s/(\d+)/<NUM_ORDI>$1<\/NUM_ORDI>/g;
#		
#		$ordinals2 =~ s/-/<CHAR>- exp\=till<\/CHAR>/;
#		
#		$text =~ s/$ordinals1 *<NUM_ORDI>/$ordinals2<NUM_ORDI>/;
#		
#	}

