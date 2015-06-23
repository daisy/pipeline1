#!/usr/bin/perl -w

#***************************************************************#
# homograph_disambiguation
#***************************************************************#
sub homograph_disambiguation {

#	&print_all_output();
	
	@word_list = sort ( keys ( %orthography ));

	&remove_duplicates_and_foreign();

#	&print_all_output();


	&disamb_dt_jj_nn();
	&disamb_jj_nn();
	&disamb_dt_nn();
	&disamb_nn_vb();
	&disamb_nn_vb_2();
	&disamb_vb_ab();
	&disamb_gen_ind();
	&disamb_infinitive();
}
#***************************************************************#
# remove_duplicates_and_foreign
#***************************************************************#
sub remove_duplicates_and_foreign {
	
	# Remove foreign words.
	my $counter = 0;
	foreach my $loc (@word_list) {
		
		# PoS field contains at least two fields.
		# Remove fields with identical PoS, morphology and transcriptions.
		if ( $pos{ $loc }	=~	/\|/ ) {
			
			&remove_duplicates($loc);
						
		} # end if $lang

		# Language field contains at least two fields and the main language.
		# Remove fields with foreign lanugages.
		if ( $lang{ $loc }	=~	/(?:\|$main_lang|$main_lang\|)/ ) {
			&remove_foreign($loc);
						
		} # end if $lang

		$counter++;
	}
}
#***************************************************************#
# disamb_dt_jj_nn
#***************************************************************#
sub disamb_dt_jj_nn {
	# DT JJ NN
	$counter = 0;
	foreach my $loc (@word_list) {
	
	
		if (
			$counter			>	3
			&&
			$pos{ $loc }			=~	/NN/
			&&
			$pos{ $word_list[$counter-2] }	=~	/JJ/
			&&
			$pos{ $word_list[$counter-4] }	=~	/DT/
		) {
			&dt_jj_nn($word_list[$counter-4],$word_list[$counter-2],$loc);
						
		} # end if $lang
		$counter++;
	} # end foreach $loc
}
#***************************************************************#
# disamb_jj_nn
#***************************************************************#
sub disamb_jj_nn {
	# JJ NN
	$counter = 0;
	foreach my $loc (@word_list) {
		# JJ NN
		if (
			$counter			>	1
			&&
			$pos{ $loc }			=~	/NN/
			&&
			$pos{ $word_list[$counter-2] }	=~	/JJ/
		) {
			&jj_nn($word_list[$counter-2],$loc);
						
		} # end if $lang
		$counter++;
	}
}
#***************************************************************#
# disamb_dt_nn
#***************************************************************#
sub disamb_dt_nn {
	# DT NN
	$counter = 0;
	foreach my $loc (@word_list) {

		if (
			$counter			>	1
			&&
			$pos{ $loc }			=~	/(?:NN|NUM)/
			&&
			$pos{ $word_list[$counter-2] }	=~	/DT/
		) {
			&dt_nn($word_list[$counter-2],$loc);
						
		} # end if $lang
	$counter++;
	} # end foreach $loc
}
#***************************************************************#
# disamb_nn_vb
#***************************************************************#
sub disamb_nn_vb {
	# NN/JJ VB
	$counter = 0;
	foreach my $loc (@word_list) {

		if (
			$counter + 2			<=	$#word_list
			&&
			$pos{ $loc }			=~	/(?:NN.*JJ|JJ.*NN)/
			&&
			$pos{ $word_list[$counter+2] }	=~	/VB/
		) {
			&nn_vb($loc,$word_list[$counter+2]);
						
		} # end if $lang
	$counter++;
	} # end foreach $loc
}
#***************************************************************#
# disamb_nn_vb_2
#***************************************************************#
sub disamb_nn_vb_2 {
	# First word in string is NN followd by VB.
	if (
		$#word_list			>	1
		&&
		$pos{ $word_list[0] }		=~	/(?:NN\||\|NN)/
		&&
		$pos{ $word_list[2] }		=~	/VB/
	) {
		&nn_vb_2($word_list[0],$word_list[2]);
						
	} # end if
}
#***************************************************************#
# disamb_vb_ab
#***************************************************************#
sub disamb_vb_ab {
	# VB AB/??
	$counter = 0;
	foreach my $loc (@word_list) {

		if (
			$counter			>	1
			&&
			$pos{ $loc }			=~	/(?:AB\||\|AB)/
			&&
			$pos{ $word_list[$counter-2] }	=~	/VB/
		) {
			&vb_ab($word_list[$counter-2],$loc);
						
		} # end if $lang
	$counter++;
	} # end foreach $loc
}	
#***************************************************************#
# disamb_gen_ind
#***************************************************************#
sub disamb_gen_ind {
	# GEN NN --> IND
	$counter = 0;
	foreach my $loc (@word_list) {

		if (
			$counter			>	1
			&&
			$pos{ $loc }			=~	/(?:NN\||\|NN)/
			&&
			(
				$morphology{ $word_list[$counter-2] }	=~	/GEN/
				||
				$pos{ $word_list[$counter-2] }		eq	"PS"
			)
		) {
			&gen_ind($word_list[$counter-2],$loc);
						
		} # end if $lang
	$counter++;
	} # end foreach $loc

}
#***************************************************************#
# disamb_inf
#***************************************************************#
sub disamb_infinitive {
	$counter = 0;
	foreach my $loc (@word_list) {

		if (
			$counter + 2			<=	$#word_list
			&&
			$pos{ $loc }			=~	/(?:IE\||\|IE)/
			&&
			$pos{ $word_list[$counter+2] }	=~	/VB/
		) {
			&infinitive($loc,$word_list[$counter+2],);
						
		} # end if $lang
	$counter++;
	} # end foreach $loc
}
#***************************************************************#
# infinitive
#
# Example:	att tillkalla
#
#***************************************************************#
sub infinitive {
	
	my ($curr,$foll) = @_;
	
	my @ie		=	split/\|/,$pos{ $curr };
	my @ie_mo	=	split/\|/,$morphology{ $curr };

	my @vb		=	split/\|/,$pos{ $foll };
	my @vb_mo	=	split/\|/,$morphology{ $foll };

	my $change_apply	=	0;

	my @position1	=	();
	my @position2	=	();

	my $ie_counter	=	0;
	foreach my $i (@ie) {


		# Find infinitive pos.
		if ( 
			$i			=~	/IE/
		) {

			my $vb_counter = 0;

			foreach my $v ( @vb_mo ) {
				
				if (
					$v			=~	/^INF/
				) {
					
#					print "THIS IS THE THING:\n$i\t$ie_mo[$ie_counter]\n$v\t$vb_mo[$vb_counter]\n\n";

					# Save the position that should be saved.
					push @position1,$ie_counter;
					push @position2,$vb_counter;
							
					$change_apply = 1;
				

				}
				$vb_counter++;
			} # end foreach $n
		 	
		}
				
		$ie_counter++;
	}

	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $curr,$p1 );
		&change_homo_field( $foll,$p2 );
	}

}
#***************************************************************#
# gen_ind
#
# Example:	flickans planet
#		deras banan
#***************************************************************#
sub gen_ind {

	my ($prev,$curr) = @_;
	
	my @gen		=	split/\|/,$pos{ $prev };
	my @gen_mo	=	split/\|/,$morphology{ $prev };

	my @nn		=	split/\|/,$pos{ $curr };
	my @nn_mo	=	split/\|/,$morphology{ $curr };

	$change_apply	=	0;

	my @position1	=	();
	my @position2	=	();

	my $gen_counter	=	0;
	foreach my $g (@gen) {

#		print "G: $g\t$gen_mo[$gen_counter]\n";


		# Find delimiter morhpology.
		if ( 
			$gen_mo[$gen_counter]	=~	/GEN/
			||
			$g			eq	"PS"
		) {

			my $nn_counter = 0;

			foreach my $n ( @nn ) {
				
				if (
					$n			eq	"NN"
					&&
					$nn_mo[$nn_counter]	=~	/IND/
				) {
					
					# Save the position that should be saved.
					push @position1,$gen_counter;
					push @position2,$nn_counter;
					$change_apply = 1;
				}
					
				
				$nn_counter++;
			} # end foreach $n
		 	
		}
				
		$gen_counter++;
	}

	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $prev,$p1 );
		&change_homo_field( $curr,$p2 );
	}
	
	
	
#	&change_homo_field( $curr,$nn_counter);

}
#***************************************************************#
# dt_jj_nn
#
# Disambiguating DT-JJ-NN combinations.
#
# Example:	en plan banan
#		ett plant plan
#
#***************************************************************#
sub dt_jj_nn {

	my ($prev2,$prev1,$curr) = @_;
	
	my @nn		=	split/\|/,$pos{ $curr };
	my @nn_mo	=	split/\|/,$morphology{ $curr };

	my @jj		=	split/\|/,$pos{ $prev1 };
	my @jj_mo	=	split/\|/,$morphology{ $prev1 };

	my @dt		=	split/\|/,$pos{ $prev2 };
	my @dt_mo	=	split/\|/,$morphology{ $prev2 };

	my $change_apply =	0;

	my @position1	=	();
	my @position2	=	();
	my @position3	=	();
	
	my $jj_counter	=	0;
	foreach my $j (@jj) {

		# Find delimiter morhpology.
		if ( 
			$j			eq	"JJ"
			&&
			$jj_mo[$jj_counter]	=~	/^(?:...) (...) SIN IND/
		) {
			my $gender	=	$1;
			my $nn_counter = 0;

			foreach my $n ( @nn ) {
				
				if (
					$n			eq	"NN"
					&&
					$nn_mo[$nn_counter]	=~	/^$gender SIN IND/
				) {
		
					my $dt_counter = 0;
			
					foreach my $d ( @dt ) {
						
#						print "D $d\n";
						
						if (
							$d			eq	"DT"
							&&
							$dt_mo[$dt_counter]	=~	/^$gender SIN IND/
						) {
					
#							print "THIS IS THE THING:\n$d\t$dt_mo[$dt_counter]\n$j\t$jj_mo[$jj_counter]\n$n\t$nn_mo[$nn_counter]\n\n";
					
							# Save the position that should be saved.
							push @position1,$dt_counter;
							push @position2,$jj_counter;
							push @position3,$nn_counter;
							
							$change_apply = 1;

						}
						$dt_counter++;
						
					} # end foreach $dt
				} # end foreach $nn
				
				$nn_counter++;
			} # end foreach $n
		 	
		}
				
		$jj_counter++;
	}

	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
		my $p3 = join"|",@position3;
	
		&change_homo_field( $prev2,$p1 );
		&change_homo_field( $prev1,$p2 );
		&change_homo_field( $curr,$p3 );
	}

}
#***************************************************************#
# jj_nn
#
# Disambiguating JJ-NN combinations.
#
# Example:	en plan banan
#		ett plant plan
#
#***************************************************************#
sub jj_nn {

	my ($prev,$curr) = @_;
	
	my @nn		=	split/\|/,$pos{ $curr };
	my @nn_mo	=	split/\|/,$morphology{ $curr };

	my @jj		=	split/\|/,$pos{ $prev };
	my @jj_mo	=	split/\|/,$morphology{ $prev };

	my $change_apply	=	0;

	my @position1	=	();
	my @position2	=	();

	my $jj_counter	=	0;
	foreach my $j (@jj) {

		# Find delimiter morhpology.
		if ( 
			$j			eq	"JJ"
			&&
			$jj_mo[$jj_counter]	=~	/^(?:...) (...) SIN IND/
		) {
			my $gender	=	$1;
			my $nn_counter = 0;

			foreach my $n ( @nn ) {
				
				if (
					$n			eq	"NN"
					&&
					$nn_mo[$nn_counter]	=~	/^$gender SIN IND/
				) {
					
#					print "THIS IS THE THING:\n$j\t$jj_mo[$jj_counter]\n$n\t$nn_mo[$nn_counter]\n\n";

					# Save the position that should be saved.
					push @position1,$jj_counter;
					push @position2,$nn_counter;
							
					$change_apply = 1;

				}
				
				$nn_counter++;
			} # end foreach $n
		 	
		}
				
		$jj_counter++;
	}


	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $prev,$p1 );
		&change_homo_field( $curr,$p2 );
	}

}
#***************************************************************#
# dt_nn
#
# Disambiguating DT-NN combinations.
#
# Example:	en banan
#		ett plan
#		den 23 november
#
#***************************************************************#
sub dt_nn {

	my ($prev,$curr) = @_;
	
	my @nn		=	split/\|/,$pos{ $curr };
	my @nn_mo	=	split/\|/,$morphology{ $curr };

	my @dt		=	split/\|/,$pos{ $prev };
	my @dt_mo	=	split/\|/,$morphology{ $prev };

	my $change_apply	=	0;

	my @position1	=	();
	my @position2	=	();


	my $dt_counter	=	0;
	foreach my $d (@dt) {


		# Find delimiter morhpology.
		if ( 
			$d			eq	"DT"
			&&
			$dt_mo[$dt_counter]	=~	/^(...) SIN (...)/
		) {
			my $gender	=	$1;
			my $species	=	$2;
			my $nn_counter = 0;

			foreach my $n ( @nn ) {
				
				
				if (
					(
						$n			eq	"NN"
						&&
						$nn_mo[$nn_counter]	=~	/^$gender SIN $species/
					) || (
						$n			=~	/NUM/
					)
				) {
					
#					print "THIS IS THE THING:\n$d\t$dt_mo[$dt_counter]\n$n\t$nn_mo[$nn_counter]\n\n";
					# Save the position that should be saved.
					push @position1,$dt_counter;
					push @position2,$nn_counter;
							
					$change_apply = 1;

				}
				$nn_counter++;
			} # end foreach $n
		 	
		}
				
		$dt_counter++;
	}

	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $prev,$p1 );
		&change_homo_field( $curr,$p2 );
	}
}
#***************************************************************#
# remove_foreign
#
# Removes fields with foreign language tags.
# Do not use splice since the fields are recursively removed.
#***************************************************************#
sub remove_foreign {

	my $loc = shift;
	
	
	my @langs	=	split/\|/,$lang{ $loc };
	
	my @remove_loc	=	();
	my $rem_counter	=	0;
	
	
	# Get the location of the field that should be removed.
	foreach my $l ( @langs ) {
		if ( $l !~ /$main_lang/ ) {
			push @remove_loc,$rem_counter;
		}
		$rem_counter++;		
	} # end foreach @lang
		
	my $remove_loc	=	join"|",@remove_loc;

	
	my @pos		=	split/\|/,$pos{ $loc };
	my @pos2	=	();	
	my @morph	=	split/\|/,$morphology{ $loc };
	my @morph2	=	();	
	my @transcr	=	split/\|/,$transcription{ $loc };
	my @transcr2	=	();	
	my @lang	=	split/\|/,$lang{ $loc };
	my @lang2	=	();	
	
#	print "TT $orthography{$loc}\t@transcr2\n";
	
	$counter	=	0;

	# Remove from %pos, %morphology, %transcription and %lang:
	foreach my $p ( @pos ) {
		if ( $counter	!~	/^(?:$remove_loc)$/ ) {
			push @pos2,$pos[$counter];
			push @morph2,$morph[$counter];
			push @transcr2,$transcr[$counter];
			push @lang2,$lang[$counter];
		}
		$counter++;
	} # end foreach @pos


	# Change the hashes.
	$pos{ $loc }		=	join"|",@pos2;
	$morphology{ $loc }	=	join"|",@morph2;
	$transcription{ $loc }	=	join"|",@transcr2;
	$lang{ $loc }		=	join"|",@lang2;
	
	
}
#***************************************************************#
# remove_duplicates
#
# Remove fields with identical PoS, morphology and transcriptions.
# Do not use splice since the fields are recursively removed.
#***************************************************************#
sub remove_duplicates {

	my $loc		= 	shift;


	my @pos		=	split/\|/,$pos{ $loc };
	my @pos2	=	();	
	my @morph	=	split/\|/,$morphology{ $loc };
	my @morph2	=	();	
	my @transcr	=	split/\|/,$transcription{ $loc };
	my @transcr2	=	();	
	my @lang	=	split/\|/,$lang{ $loc };
	my @lang2	=	();	
	
	my %morph_check	=	();
	
	
	my $counter	=	0;
	my $changed	=	0;
	
	
	# Check if there are identical morphology fields for the word.
	foreach my $m ( @morph ) {

	
		# Identical morphology exists.
		if ( exists ( $morph_check{ $m } )) {
			
			# $m_loc1 = the place in list the same morph string was found earlier.			
			my $m_loc1	=	$morph_check{ $m };

			
			# If PoS and transcriptions fields at the same locations are identical.
			if ( 
				$pos[ $m_loc1 ]		eq	$pos[ $counter ]
				&&	
				$transcr[ $m_loc1 ]	eq	$transcr[ $counter ]
			) {

#				print "SAME: $m\t$morph_check{$m}\n$pos[$m_loc1]\t$pos[$counter]\n$transcr[$m_loc1]\t$transcr[$counter]\n\n";

				# Flag to replace the hash values with one single value below.
				$changed	=	1;
				
				
				# Remove list field from %pos, %morphology, %transcription and %lang:
				my $rcounter	=	0;
				foreach my $p ( @pos ) {
					
					# Make new lists for all fields that are not the current field (that will be removed).
					if ( $rcounter	ne	$counter ) {
						push @pos2,$pos[$rcounter];
						push @morph2,$morph[$rcounter];
						push @transcr2,$transcr[$rcounter];
						push @lang2,$lang[$rcounter];
					}
					$rcounter++;
				} # end foreach @pos
			
			}
			
		} else {
			$morph_check{ $m } = $counter;
		
		} # end exists
	
		$counter++;
	
	} # end foreach @morph
	
#	print "LOC: $loc\t$pos{ $loc}\t@pos2\n";

	if ( $changed	==	1 ) {
		# Change the hashes.
		$pos{ $loc }		=	join"|",@pos2;
		$morphology{ $loc }	=	join"|",@morph2;
		$transcription{ $loc }	=	join"|",@transcr2;
		$lang{ $loc }		=	join"|",@lang2;
	}

#	print "TT $transcription{$loc}\n";
}
#***************************************************************#
# nn_vb
#
# Example:	modern skrattar
#
#***************************************************************#
sub nn_vb {

	my ($curr,$foll) = @_;
	
	my @nn		=	split/\|/,$pos{ $curr };
	my @nn_mo	=	split/\|/,$morphology{ $curr };

	my @vb		=	split/\|/,$pos{ $foll };
	my @vb_mo	=	split/\|/,$morphology{ $foll };

	my $change_apply	=	0;

	my @position1	=	();
	my @position2	=	();

	my $nn_counter	=	0;
	foreach my $n (@nn) {

		# Find noun morhpology.
		if ( 
			$n			eq	"NN"
		) {

			my $vb_counter = 0;

			foreach my $v ( @vb ) {
				
				if (
					$v			eq	"VB"
				) {
					
#					print "THIS IS THE THING:\n$d\t$dt_mo[$dt_counter]\n$n\t$nn_mo[$nn_counter]\n\n";

					# Save the position that should be saved.
					push @position1,$nn_counter;
					push @position2,$vb_counter;
							
					$change_apply = 1;
				

				}
				$vb_counter++;
			} # end foreach $n
		 	
		}
				
		$nn_counter++;
	}

	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $curr,$p1 );
		&change_homo_field( $foll,$p2 );
	}

}
#***************************************************************#
# nn_vb_2
#
# Example:	planet flyger
#
#***************************************************************#
sub nn_vb_2 {

	my ($curr,$foll) = @_;

#	print "C: $curr\tF: $foll\n";
	
	my @nn		=	split/\|/,$pos{ $curr };
	my @nn_mo	=	split/\|/,$morphology{ $curr };

	my @vb		=	split/\|/,$pos{ $foll };
	my @vb_mo	=	split/\|/,$morphology{ $foll };
	
	my $change_apply	=	0;

	my @position1	=	();
	my @position2	=	();

	my $nn_counter	=	0;
	foreach my $n (@nn) {

		# Find noun morhpology.
		if ( 
			$n			eq	"NN"
			&&
			$nn_mo[$nn_counter]		=~	/DEF/
		) {

			my $vb_counter = 0;

			foreach my $v ( @vb ) {
				
				if (
					$v			eq	"VB"
				) {
					
#					print "THIS IS THE THING:\n$v\t$vb_mo[$vb_counter]\n$n\t$nn_mo[$nn_counter]\n\n";

					# Save the position that should be saved.
					push @position1,$nn_counter;
					push @position2,$vb_counter;
							
					$change_apply = 1;

				}
				$vb_counter++;
			} # end foreach $n
		 	
		}
				
		$nn_counter++;
	}
	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $curr,$p1 );
		&change_homo_field( $foll,$p2 );
	}

}	
#***************************************************************#
# vb_ab
#
# Example:	åker fort
#
#***************************************************************#
sub vb_ab {
	
	my ($prev,$curr) = @_;

	
	my @vb		=	split/\|/,$pos{ $prev };
	my @vb_mo	=	split/\|/,$morphology{ $prev };
	
	my @ab		=	split/\|/,$pos{ $curr };
	my @ab_mo	=	split/\|/,$morphology{ $curr };

	my $change_apply	=	0;

	my @position1	=	();
	my @position2	=	();

	my $ab_counter	=	0;
	foreach my $a ( @ab ) {
		
		if (
			$a		eq	"AB"
		) {
			
			my $vb_counter = 0;
			foreach my $v ( @vb ) {
				
				if (
					$v	eq	"VB"
				) {
					# Save the position that should be saved.
					push @position1,$ab_counter;
					push @position2,$vb_counter;
							
					$change_apply = 1;
				}
				$vb_counter++;
			} # end foreach @vb
		}
		$ab_counter++;
	}
	
	if ( $change_apply	==	1 ) {
		
		my $p1 = join"|",@position1;
		my $p2 = join"|",@position2;
	
		&change_homo_field( $prev,$p1 );
		&change_homo_field( $curr,$p2 );
	}
}
#***************************************************************#
# change_homo_fiels
#
# Removes the unlikely list fields of a location.
#
#***************************************************************#
sub change_homo_field {
	
	my ($loc,$position)	=	@_;

#	print "\n----------\nLOC: $loc\tPOS: $position\n\n\n";
	
	my @pos2		=	split/\|/,$pos{ $loc };

	my @morph2		=	split/\|/,$morphology{ $loc };
	my @lang2		=	split/\|/,$lang{ $loc };
	my @transcr2		=	split/\|/,$transcription{ $loc };
	my @pos			=	();
	my @morph		=	();
	my @transcr		=	();
	my @lang		=	();

	my $counter		=	0;
	foreach my $pos (@pos2) {

#		print "POSPOSOPS $counter\t\t$position\n\n";		
		
		if ($counter	=~	/^(?:$position)$/ ) {
			
			push @pos,$pos2[$counter];
			push @morph,$morph2[$counter];
			push @transcr,$transcr2[$counter];
			push @lang,$lang2[$counter];
		}
		$counter++;
	}

	$pos{ $loc }		=	join"|",@pos;
	$morphology{ $loc }	=	join"|",@morph;
	$lang{ $loc }		=	join"|",@lang;
	$transcription{ $loc }	=	join"|",@transcr;
	
	
}
#***************************************************************#
#***************************************************************#
1;
