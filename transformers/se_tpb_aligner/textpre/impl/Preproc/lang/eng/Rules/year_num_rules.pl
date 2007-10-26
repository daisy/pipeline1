#!/usr/bin/perl -w

#**************************************************************#
sub year_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&year_interval_num_1();
	&year_interval_num_2();
	&year_interval_num_3();
	&year_interval_num_4();

	&year_num_1();
	&year_num_2();
	&year_num_3();
	&year_num_4();
	&year_num_5();
	&year_num_6();

}
#**************************************************************#
# year_num_1
#
# Year interval with preceding time word or proper name.
# Example:		in 1986 - 1998
#			winter 2000/2001
#			Nilsson 2001-2008
#
#**************************************************************#
sub year_interval_num_1 {

	# With blanks.
	if (exists ( $ort{ $prev_2 } ) && exists( $ort{ $next_4 } ) ) {


		if (
			# Context
			(
				$ort{ $prev_2 }	=~	/^(?:$time_words|\©|copyright|in)$/io
				||
				$pos{ $prev_2 }	=~	/PM/
				||
				$ort{ $prev_2 }	=~	/^[A-ZÅÄÖÜ][a-zåäöü][a-zåäöü]+$/
			)
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $curr }	=~	/^$year_format$/
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^[\-\/]$/
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^(?:$year_format|$year_short_format)$/
		) {
			# Retag
			$type{ $prev_2 }		=	"YEAR INTERVAL";
			$type{ $prev_1 }		=	"YEAR INTERVAL";
			$type{ $curr }			=	"YEAR INTERVAL";
			$type{ $next_1 }		=	"YEAR INTERVAL";
			$type{ $next_2 }		=	"YEAR INTERVAL";
			$type{ $next_3 }		=	"YEAR INTERVAL";
			$type{ $next_4 }		=	"YEAR INTERVAL";
			
			$pos{ $curr }			=	"NUM YEAR";
			$pos{ $next_4 }			=	"NUM YEAR";
			
			$exp{ $next_2 }			=	"to";
			
		}

	} # end exists

	#---------------------------------------------#
	# Without blanks.
	if (exists ( $ort{ $prev_2 } ) && exists( $ort{ $next_2 } ) ) {

		if (
			# Context
			(
				$ort{ $prev_2 }	=~	/^(?:$time_words|\©|copyright|in)$/io
				||
				$pos{ $prev_2 }	=~	/PM/
				||
				$ort{ $prev_2 }	=~	/^[A-ZÅÄÖÜ][a-zåäöü][a-zåäöü]+$/
			)
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $curr }	=~	/^$year_format$/
			&&
			$ort{ $next_1 }	=~	/^[\-\/]$/
			&&
			$ort{ $next_2 }	=~	/^(?:$year_format|$year_short_format)$/
		) {
			# Retag
			$type{ $prev_2 }		=	"YEAR INTERVAL";
			$type{ $prev_1 }		=	"YEAR INTERVAL";
			$type{ $curr }			=	"YEAR INTERVAL";
			$type{ $next_1 }		=	"YEAR INTERVAL";
			$type{ $next_2 }		=	"YEAR INTERVAL";
			
			$pos{ $curr }			=	"NUM YEAR";
			$pos{ $next_2 }			=	"NUM YEAR";
			
			$exp{ $next_1 }			=	"to";
			
		}

	} # end exists
}
#**************************************************************#
# year_interval_2
#
# Year interval surrounded by parentheses or a closuring parenthesis.
# Example:		(1986 - 1998)
#			(Wallin 1718-63)
#
#**************************************************************#
sub year_interval_num_2 {

	# With blanks.
	if (exists ( $ort{ $next_5 } ) ) {

		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^[\-\/]$/
			&&
			$pos{ $next_3 }		eq	"DEL"
			&&
			$ort{ $next_4 }		=~	/^(?:$year_format|$year_short_format)$/
			&&
			$ort{ $next_5 }		eq	")"
		) {
			# Retag
			$type{ $curr }		=	"YEAR INTERVAL";
			$type{ $next_1 }	=	"YEAR INTERVAL";
			$type{ $next_2 }	=	"YEAR INTERVAL";
			$type{ $next_3 }	=	"YEAR INTERVAL";
			$type{ $next_4 }	=	"YEAR INTERVAL";
						
			$pos{ $curr }		=	"NUM YEAR";
			$pos{ $next_4 }		=	"NUM YEAR";
			
			$exp{ $next_2 }		=	"to";

		}

	} # end exists
	
	#---------------------------------------------#
	# Without blanks.
	if (exists ( $ort{ $prev_1 } ) && exists( $ort{ $next_3 } ) ) {

		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$ort{ $next_1 }		=~	/^[\-\/]$/
			&&
			$ort{ $next_2 }		=~	/^(?:$year_format|$year_short_format)$/
			&&
			$ort{ $next_3 }		eq	")"
		) {
			# Retag
			$type{ $curr }		=	"YEAR INTERVAL";
			$type{ $next_1 }	=	"YEAR INTERVAL";
			$type{ $next_2 }	=	"YEAR INTERVAL";
			
			$pos{ $curr }		=	"NUM YEAR";
			$pos{ $next_2 }		=	"NUM YEAR";
			
			$exp{ $next_1 }		=	"to";
			
		}

	} # end exists
}
#**************************************************************#
# year_interval_num_3
#
# Year interval followed by "B.C"
# Example:		1500-1200 B.C.
#			
#
#**************************************************************#
sub year_interval_num_3 {

	# With blanks.
	if (exists ( $ort{ $next_6 } ) ) {

		if (
			# Context
			$ort{ $curr }	=~	/^$year_format$/
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^[\-\/]$/
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^(?:$year_format|$year_short_format)$/
			&&
			$pos{ $next_5 }	eq	"DEL"
			&&
			$ort{ $next_6 }	=~	/^B\.C\.?$/i

		) {
			# Retag
			$type{ $curr }			=	"YEAR INTERVAL";
			$type{ $next_1 }		=	"YEAR INTERVAL";
			$type{ $next_2 }		=	"YEAR INTERVAL";
			$type{ $next_3 }		=	"YEAR INTERVAL";
			$type{ $next_4 }		=	"YEAR INTERVAL";
			
			$pos{ $curr }			=	"NUM YEAR";
			$pos{ $next_4 }			=	"NUM YEAR";
			
			$exp{ $next_2 }			=	"to";
			
		}

	} # end exists

	#---------------------------------------------#
	# Without blanks.
	if (exists (  $ort{ $next_4 } ) ) {

		if (
			# Context
			$ort{ $curr }	=~	/^$year_format$/
			&&
			$ort{ $next_1 }	=~	/^[\-\/]$/
			&&
			$ort{ $next_2 }	=~	/^(?:$year_format|$year_short_format)$/
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^B\.C\.?$/i
		) {
			# Retag
			$type{ $curr }			=	"YEAR INTERVAL";
			$type{ $next_1 }		=	"YEAR INTERVAL";
			$type{ $next_2 }		=	"YEAR INTERVAL";
			
			$pos{ $curr }			=	"NUM YEAR";
			$pos{ $next_2 }			=	"NUM YEAR";
			
			$exp{ $next_1 }			=	"to";
			
		}

	} # end exists
}
#**************************************************************#
# year_interval_num_4
#
# Example:		between 1950 and 1960
#			
#
#**************************************************************#
sub year_interval_num_4 {

	if (
		exists ( $ort{ $prev_2 } )
		&&
		exists ( $ort{ $next_4 } )
	) {

		if (
			# Context
			$ort{ $curr }	=~	/^$year_format$/
			&&
			$ort{ $prev_2 }	eq	"between"
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	eq	"and"
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^(?:$year_format|$year_short_format)$/
		) {
			# Retag
			$type{ $curr }			=	"YEAR INTERVAL";
			$type{ $next_1 }		=	"YEAR INTERVAL";
			$type{ $next_2 }		=	"YEAR INTERVAL";
			$type{ $next_3 }		=	"YEAR INTERVAL";
			$type{ $next_4 }		=	"YEAR INTERVAL";
			
			$pos{ $curr }			=	"NUM YEAR";
			$pos{ $next_4 }			=	"NUM YEAR";
			
			
		}

	} # end exists
}
#**************************************************************#
# year_num_1
#
# Year with preceding time word or proper name.
# Example:		våren 1995
#
#**************************************************************#
sub year_num_1 {

	if (exists ( $ort{ $prev_2 } ) ) {

		if (
			# Context
			(
				$ort{ $prev_2 }	=~	/^(?:$time_words|\©|copyright|in)$/io
				||
				$pos{ $prev_2 }	=~	/PM/
				||
				$ort{ $prev_2 }	=~	/^[A-ZÅÄÖÜ][a-zåäöü][a-zåäöü]+$/
			)
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $curr }	=~	/^$year_format$/
		) {
			# Retag
			$type{ $curr }			=	"YEAR";
			
			$pos{ $curr }			=	"NUM YEAR";
		}

	} # end exists
}
#**************************************************************#
# year_num_2
#
# Year within parentheses or right parenthesis following.
# Example:		(2001)
#			(Rosdal, 1456)
#
#**************************************************************#
sub year_num_2 {

	if (exists( $ort{ $next_1 } )) {

		if (
			# Context
			$ort{ $curr }	=~	/^$year_format$/
			&&
			$ort{ $next_1 }	eq	")"
		) {
			# Retag
			$type{ $curr }			=	"YEAR";
			
			$pos{ $curr }			=	"NUM YEAR";
		}

	} # end exists
}
#**************************************************************#
# year_num_3
#
# Example:		2001/02:14
#
#**************************************************************#
sub year_num_3 {

	# With blanks.
	if (exists( $ort{ $next_4 } )) {

		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^[\:\/]$/
			&&
			$pos{ $next_3 }		eq	"DEL"
			&&
			$ort{ $next_4 }		=~	/^\d+$/
		) {
			# Retag
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";
			
			$type{ $next_2 }	.=	"|EXPAND";
		}
	} # end exists

	#-------------------------------------------#
	# Without blanks.
	if (exists( $ort{ $next_2 } )) {

		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$ort{ $next_1 }		=~	/^[\:\/]$/
			&&
			$ort{ $next_2 }		=~	/^\d+$/
		) {
			# Retag
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";

			$type{ $next_1 }	.=	"|EXPAND";
		}
	} # end exists
}
#**************************************************************#
# year_num_4
#
# Example:		/2006
#
#**************************************************************#
sub year_num_4 {

	# With blanks.
	if (exists( $ort{ $prev_2 } )) {

		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$ort{ $prev_2 }		eq	"/"
		) {
			# Retag
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";
		}
	} # end exists

	#-------------------------------------------#
	# Without blanks.
	if (exists( $ort{ $prev_1 } )) {

		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$ort{ $prev_1 }		eq	"/"
		) {
			# Retag
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";
		}
	} # end exists
}
#**************************************************************#
sub year_num_5 {

#	print "\n
#	--------
#	year_num_5
#	Curr:	$ort{ $curr }
#	Next1:	$pos{ $next_1 }
#	Next2:	$pos{ $next_2 }
#	\n";

	#-------------------------------------------#
	# With blanks.
	if (exists( $ort{ $next_2 } )) {
		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^[fe]\. ?Kr\.?$/i
		) {
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";
		}
	} # end exists
	
	#-------------------------------------------#
	# Without blanks.
	if (exists( $ort{ $next_1 } )) {
		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$ort{ $next_1 }		=~	/^[fe]\. ?Kr\.?$/i
		) {
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";
		}
	} # end exists
}
#**************************************************************#
# year_num_6
#
# 1950s		-->	nineteen fiftie(s)
#**************************************************************#
sub year_num_6 {

#	print "\n
#	--------
#	year_num_6
#	Prev1:	$pos{ $prevt_1 }
#	Curr:	$ort{ $curr }
#	\n";

	#-------------------------------------------#
	# With blanks.
	if (exists( $ort{ $prev_1 } )) {
		if (
			# Context
			$ort{ $curr }		=~	/^$year_format$/
			&&
			$ort{ $prev_1 }		eq	"s"
		) {
			$type{ $curr }		=	"YEAR";
			
			$pos{ $curr }		=	"NUM YEAR";
		}
	} # end exists
	
}
#**************************************************************#

#**************************************************************#
1;
