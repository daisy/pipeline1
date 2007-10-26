#!/usr/bin/perl -w


sub url_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&url_1();
	
}

#**************************************************************#
# url_1
# Example:	www.tpb.se
#
#**************************************************************#
sub url_1 {
#print "N2 $next_2\n";
	if (exists ($ort{ $next_2 } )) {

#		print "\n\n------------------\n
#		url_1
#		Curr: $ort{ $curr }
#		Next1: $ort{ $next_1 }
#		Next2: $ort{ $next_2 }
#		\n\n";


			# Context
		if (
			$ort{ $curr }		=~	/^(?:www|https?)$/
			&&
			$ort{ $next_1 }		=~	/^[\:\.\/]+$/
		
		) {	


			my @index_list		=	split/\|/,$index_list;
			@index_list		=	sort(@index_list);

			my $saved_counter 	=	$curr;

		
			# Tag right context fields as "URL" until next "DEL"
			$pos_tester		=	0;
			my $rc_check		=	0;
			my $counter		=	0;
			
			
			foreach my $rc (@index_list) {

				if ($rc eq "$saved_counter") {
					$rc_check = 1;
				}

				if (
					$rc		=~	/\d/
					&&
					$rc_check	==	1
					&&
					$pos_tester	==	0
				) {
					if ( 
						$pos{ $index_list[ $counter ] }	=~	/^(?:(?:MAJ|MIN)[ _]DEL|QUOTE)$/
						&&
						(
							$index_list[ $counter + 1 ]		!~	/\d/
							||
							$pos{ $index_list[ $counter + 1 ] }	=~	"DEL"
						)
					) {	
						$pos_tester	=	1;			
					
					} elsif (
						$pos{ $rc }	ne	"DEL"
					) {
								
						$type{ $rc }	=	"URL";

						# Domain							
						if ( $ort{ $rc }	=~	/^$domain_list$/i ) {
							&domain_rule($rc);
						}

					} else {
						$pos_tester	=	1;
					}
#						print "\n\n--------\nRC: $rc\nORT: $ort{$rc}\nPT: $pos_tester\nCOU: $counter\t$saved_counter\nP1: $pos{ $index_list[ $counter ] }\nP2: $index_list[ $counter + 1 ]\n\n";


				}
				$counter++;
			} # end foreach @index_list

		}
	
	}

}
1;
