#! /usr/bin/perl -w


#**************************************************************#
sub unit_subs {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&unit_1();
}
#**************************************************************#
# unit_1
#
#**************************************************************#
sub unit_1 {

	# <NUM> <UNIT> or <NUM_WORD> <UNIT>
	# With blanks
	if ( exists ( $ort{ $prev_2 } )) {
		
		if (


			# 12 mHz	tolv mHz
			$ort{ $curr }		=~	/^(?:$unit_list|\%)$/i
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			(
				$pos{ $prev_2 }	=~	/NUM/
				||
				$ort{ $prev_2 }	=~	/(?:$num_words)$/i
			)
		) {

			$type{ $curr }		=	"UNIT";
			$pos{ $curr }		=	"UNIT";

			$type{ $prev_2 }	=	"NUM CARD";
			$pos{ $prev_2 }		=	"NUM CARD";
		}
	} # end exists
	
	# Without blanks
	if ( exists ( $ort{ $prev_1 } )) {
		
		if (
		
			
			# 12mHz		tolvmHz
			$ort{ $curr }		=~	/^(?:$unit_list|\%)$/i
			&&
			(
				$pos{ $prev_1 }	=~	/NUM/
				||
				$ort{ $prev_1 }	=~	/(?:$num_words)$/i
			)
		) {
			$type{ $curr }		=	"UNIT";
			$pos{ $curr }		=	"UNIT";

			$type{ $prev_1 }	=	"NUM CARD";
			$pos{ $prev_1 }		=	"NUM CARD";
		}
	} # end exists	
}
#**************************************************************#

1;
